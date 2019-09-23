package ru.i_novus.config.web.provider;

import net.n2oapp.framework.api.metadata.SourceMetadata;
import net.n2oapp.framework.api.metadata.aware.NamespaceUriAware;
import net.n2oapp.framework.api.metadata.control.N2oStandardField;
import net.n2oapp.framework.api.metadata.control.plain.N2oCheckbox;
import net.n2oapp.framework.api.metadata.control.plain.N2oInputText;
import net.n2oapp.framework.api.metadata.event.action.N2oCloseAction;
import net.n2oapp.framework.api.metadata.event.action.N2oInvokeAction;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oFieldSet;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oLineFieldSet;
import net.n2oapp.framework.api.metadata.global.view.page.N2oStandardPage;
import net.n2oapp.framework.api.metadata.global.view.region.N2oCustomRegion;
import net.n2oapp.framework.api.metadata.global.view.region.N2oRegion;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oButton;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oToolbar;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.ToolbarItem;
import net.n2oapp.framework.api.register.DynamicMetadataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.model.ConfigRequest;
import ru.i_novus.config.api.model.GroupedConfigRequest;
import ru.i_novus.config.api.model.ValueTypeEnum;
import ru.i_novus.system_application.api.service.ApplicationRestService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ConfigDynamicProvider implements DynamicMetadataProvider {

    public static final String CONFIG_DYNAMIC = "configDynamic";
    private ApplicationRestService applicationRestService;

    @Autowired
    public void setApplicationRestService(ApplicationRestService applicationRestService) {
        this.applicationRestService = applicationRestService;
    }

    @Override
    public String getCode() {
        return CONFIG_DYNAMIC;
    }

    @Override
    public List<? extends SourceMetadata> read(String context) {
        N2oStandardPage page = new N2oStandardPage();
        page.setObjectId("groupedConfig");

        if (!context.equals("common-system")) {
            String appName = applicationRestService.getApplication(context).getName();
            page.setName(appName + " (" + context + ")");
        } else {
            page.setName("Общесистемные");
        }

        N2oForm form = new N2oForm();
        form.setId("groupedConfigForm");

        N2oCustomRegion region = new N2oCustomRegion();
        region.setWidgets(new N2oWidget[] {form});
        N2oStandardPage.Layout layout = new N2oStandardPage.Layout();
        layout.setRegions(new N2oRegion[] {region});
        page.setRegions(layout);

        List<GroupedConfigRequest> groupedConfigRequestList = applicationRestService.getGroupedApplicationConfig(context);

        ArrayList<NamespaceUriAware> lineFieldSetList = new ArrayList<>();
        for (GroupedConfigRequest groupedConfigRequest : groupedConfigRequestList) {
            N2oLineFieldSet lineFieldSet = new N2oLineFieldSet();
            lineFieldSet.setCollapsible(true);
            lineFieldSet.setLabel(groupedConfigRequest.getName());
            lineFieldSet.setFieldLabelLocation(N2oFieldSet.FieldLabelLocation.left);
            lineFieldSet.setFieldLabelAlign(N2oFieldSet.FieldLabelAlign.left);
            lineFieldSet.setLabelWidth("50%");

            ArrayList<NamespaceUriAware> n2oFieldList = new ArrayList<>();
            for (ConfigRequest config : groupedConfigRequest.getConfigs()) {
                if (config.getValueType().equals(ValueTypeEnum.STRING) ||
                        config.getValueType().equals(ValueTypeEnum.NUMBER)) {
                    N2oInputText inputText = new N2oInputText();
                    fillElement(inputText, config);

                    if (config.getValueType().equals(ValueTypeEnum.NUMBER)) {
                        inputText.setDomain("integer");
                    }

                    n2oFieldList.add(inputText);
                } else if (config.getValueType().equals(ValueTypeEnum.BOOLEAN)) {
                    N2oCheckbox checkbox = new N2oCheckbox();
                    fillElement(checkbox, config);
                    n2oFieldList.add(checkbox);
                }
            }

            lineFieldSet.setItems(n2oFieldList.toArray(NamespaceUriAware[]::new));
            lineFieldSetList.add(lineFieldSet);
        }

        // Передача appCode
        N2oInputText inputText = new N2oInputText();
        inputText.setId("appCode");
        inputText.setDefaultValue(context);
        inputText.setVisible(false);
        lineFieldSetList.add(inputText);
        form.setItems(lineFieldSetList.toArray(NamespaceUriAware[]::new));

        N2oToolbar toolbar = new N2oToolbar();
        toolbar.setPlace("bottomRight");

        N2oButton saveButton = new N2oButton();
        saveButton.setId("save");
        saveButton.setLabel("Сохранить");
        saveButton.setColor("primary");
        N2oInvokeAction saveAction = new N2oInvokeAction();
        saveAction.setOperationId("save");
        saveAction.setCloseOnSuccess(false);
        saveButton.setAction(saveAction);
        saveButton.setWidgetId(form.getId());

        N2oButton cancelButton = new N2oButton();
        cancelButton.setId("cancel");
        cancelButton.setLabel("Отмена");
        N2oCloseAction closeAction = new N2oCloseAction();
        cancelButton.setAction(closeAction);
        cancelButton.setWidgetId(form.getId());


        toolbar.setItems(new ToolbarItem[]{saveButton, cancelButton});
        page.setToolbars(new N2oToolbar[]{toolbar});

        return Arrays.asList(page);
    }

    private void fillElement(N2oStandardField field, ConfigRequest config) {
        field.setId("data." + config.getCode().replace(".", "@"));
        field.setLabel(config.getName());
        field.setHelp(config.getDescription());
        field.setDescription(config.getCode());
        field.setDefaultValue(config.getValue());
    }
}
