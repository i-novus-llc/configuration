package ru.i_novus.config.web.provider;

import net.n2oapp.framework.api.metadata.SourceMetadata;
import net.n2oapp.framework.api.metadata.aware.NamespaceUriAware;
import net.n2oapp.framework.api.metadata.control.plain.N2oInputText;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oFieldSet;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oLineFieldSet;
import net.n2oapp.framework.api.metadata.global.view.page.N2oSimplePage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.api.register.DynamicMetadataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.model.ConfigRequest;
import ru.i_novus.config.api.model.GroupedConfigForm;
import ru.i_novus.config.service.entity.ValueTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ConfigDynamicProvider implements DynamicMetadataProvider {

    public static final String CONFIG_DYNAMIC = "configDynamic";

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${config.service.url}")
    private String url;

    @Override
    public String getCode() {
        return CONFIG_DYNAMIC;
    }

    @Override
    public List<? extends SourceMetadata> read(String context) {
        String appCode = "lkb-rdm-frontend";

        N2oSimplePage page = new N2oSimplePage();
        N2oForm form = new N2oForm();
        form.setName("Параметры настроек" + context);
        page.setWidget(form);

        List<GroupedConfigForm> groupedConfigFormList =
                restTemplate.exchange(
                        url + "/byAppCode/" + appCode,
                        HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<GroupedConfigForm>>() {
                }).getBody();

        ArrayList<NamespaceUriAware> lineFieldSetList = new ArrayList<>();
        for (GroupedConfigForm groupedConfigForm : groupedConfigFormList) {
            N2oLineFieldSet lineFieldSet = new N2oLineFieldSet();
            lineFieldSet.setCollapsible(true);
            lineFieldSet.setLabel(groupedConfigForm.getName());
            lineFieldSet.setFieldLabelLocation(N2oFieldSet.FieldLabelLocation.left);
            lineFieldSet.setFieldLabelAlign(N2oFieldSet.FieldLabelAlign.left);
            lineFieldSet.setLabelWidth("50%");

            ArrayList<NamespaceUriAware> n2oFieldList = new ArrayList<>();
            for(ConfigRequest config : groupedConfigForm.getConfigs()) {
                if (config.getValueType().equals(ValueTypeEnum.STRING.getTitle()) ||
                config.getValueType().equals(ValueTypeEnum.NUMBER.getTitle())) {
                    N2oInputText inputText = new N2oInputText();

                    inputText.setId(config.getCode() + "_id");
                    inputText.setLabel(config.getName());
                    inputText.setHelp(config.getDescription());
                    inputText.setDescription(config.getCode());
                    inputText.setDefaultValue(config.getValue());

                    if(config.getValueType().equals(ValueTypeEnum.NUMBER.getTitle())) {
                        inputText.setDomain("integer");
                    }

                    n2oFieldList.add(inputText);
                } else {
                    // checkbox
                }
            }

            lineFieldSet.setItems(n2oFieldList.toArray(NamespaceUriAware[]::new));
            lineFieldSetList.add(lineFieldSet);
        }
        form.setItems(lineFieldSetList.toArray(NamespaceUriAware[]::new));

        return Arrays.asList(page);
    }
}
