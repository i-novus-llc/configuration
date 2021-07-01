package ru.i_novus.config.web.provider;

import net.n2oapp.framework.api.exception.N2oUserException;
import net.n2oapp.framework.api.metadata.SourceComponent;
import net.n2oapp.framework.api.metadata.SourceMetadata;
import net.n2oapp.framework.api.metadata.control.N2oHidden;
import net.n2oapp.framework.api.metadata.control.N2oStandardField;
import net.n2oapp.framework.api.metadata.control.plain.CheckboxDefaultValueEnum;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.GroupedApplicationConfig;
import ru.i_novus.config.api.model.ValueTypeEnum;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

@Component
public class ConfigDynamicProvider implements DynamicMetadataProvider {

    public static final String CONFIG_DYNAMIC = "configDynamic";

    @Value("${config.common.system.code}")
    private String commonSystemCode;

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
        if (context.contains("{") || context.contains("}"))
            return singletonList(new N2oStandardPage());

        N2oStandardPage page = new N2oStandardPage();
        page.setObjectId("groupedConfig");

        if (!context.equals(commonSystemCode)) {
            ApplicationResponse applicationResponse;
            try {
                applicationResponse = applicationRestService.getApplication(context);
            } catch (Exception e) {
                throw new N2oUserException("config.application.not.choose");
            }
            String appName = applicationResponse.getName();
            page.setName(appName + " (" + context + ")");
        } else {
            page.setName("Общесистемные");
        }
        page.setShowTitle(true);

        N2oForm form = new N2oForm();
        form.setId("groupedConfigForm");

        N2oCustomRegion region = new N2oCustomRegion();
        region.setContent(new N2oWidget[]{form});
        page.setItems(new N2oRegion[]{region});

        List<GroupedApplicationConfig> groupedApplicationConfigList = applicationRestService.getGroupedApplicationConfig(context);

        ArrayList<SourceComponent> lineFieldSetList = new ArrayList<>();
        for (GroupedApplicationConfig groupedApplicationConfig : groupedApplicationConfigList) {
            N2oLineFieldSet lineFieldSet = new N2oLineFieldSet();
            lineFieldSet.setCollapsible(true);
            lineFieldSet.setLabel(groupedApplicationConfig.getName());
            lineFieldSet.setFieldLabelLocation(N2oFieldSet.FieldLabelLocation.left);
            lineFieldSet.setFieldLabelAlign(N2oFieldSet.FieldLabelAlign.left);
            lineFieldSet.setFieldLabelWidth("50%");

            ArrayList<SourceComponent> n2oFieldList = new ArrayList<>();
            for (ConfigForm config : groupedApplicationConfig.getConfigs()) {
                if (ValueTypeEnum.STRING.equals(config.getValueType()) ||
                        ValueTypeEnum.NUMBER.equals(config.getValueType())) {
                    N2oInputText inputText = new N2oInputText();

                    fillElement(inputText, config);
                    inputText.setPlaceholder(config.getDefaultValue());
                    inputText.setDefaultValue(config.getValue());

                    if (ValueTypeEnum.NUMBER.equals(config.getValueType()))
                        inputText.setDomain("integer");

                    n2oFieldList.add(inputText);
                } else if (ValueTypeEnum.BOOLEAN.equals(config.getValueType())) {
                    N2oCheckbox checkbox = new N2oCheckbox();
                    fillElement(checkbox, config);
                    if (config.getValue() != null) {
                        checkbox.setDefaultValue(config.getValue());
                    } else {
                        checkbox.setDefaultValue(config.getDefaultValue());
                        checkbox.setUnchecked(CheckboxDefaultValueEnum.FALSE);
                    }
                    n2oFieldList.add(checkbox);
                }
            }

            lineFieldSet.setItems(n2oFieldList.toArray(SourceComponent[]::new));
            lineFieldSetList.add(lineFieldSet);
        }

        // Передача appCode
        N2oHidden n2oHidden = new N2oHidden();
        n2oHidden.setId("appCode");
        n2oHidden.setDefaultValue(context);
        n2oHidden.setVisible("false");
        lineFieldSetList.add(n2oHidden);
        form.setItems(lineFieldSetList.toArray(SourceComponent[]::new));

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

        return Collections.singletonList(page);
    }

    private void fillElement(N2oStandardField field, ConfigForm config) {
        field.setId("data." + config.getCode().replace(".", "__"));
        field.setLabel(config.getName());
        field.setHelp(config.getDescription());
        field.setDescription(config.getCode());
    }
}
