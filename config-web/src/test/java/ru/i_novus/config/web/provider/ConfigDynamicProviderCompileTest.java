package ru.i_novus.config.web.provider;

import net.n2oapp.framework.api.metadata.global.view.action.control.Target;
import net.n2oapp.framework.api.metadata.global.view.page.N2oPage;
import net.n2oapp.framework.api.metadata.meta.action.Action;
import net.n2oapp.framework.api.metadata.meta.action.invoke.InvokeAction;
import net.n2oapp.framework.api.metadata.meta.action.link.LinkActionImpl;
import net.n2oapp.framework.api.metadata.meta.control.Checkbox;
import net.n2oapp.framework.api.metadata.meta.control.Hidden;
import net.n2oapp.framework.api.metadata.meta.control.InputText;
import net.n2oapp.framework.api.metadata.meta.control.StandardField;
import net.n2oapp.framework.api.metadata.meta.fieldset.FieldSet;
import net.n2oapp.framework.api.metadata.meta.fieldset.LineFieldSet;
import net.n2oapp.framework.api.metadata.meta.page.Page;
import net.n2oapp.framework.api.metadata.meta.page.SimplePage;
import net.n2oapp.framework.api.metadata.meta.page.StandardPage;
import net.n2oapp.framework.api.metadata.meta.widget.Widget;
import net.n2oapp.framework.api.metadata.meta.widget.form.Form;
import net.n2oapp.framework.api.metadata.meta.widget.table.Table;
import net.n2oapp.framework.api.metadata.meta.widget.toolbar.AbstractButton;
import net.n2oapp.framework.api.metadata.meta.widget.toolbar.PerformButton;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.N2oAllDataPack;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.register.JavaInfo;
import net.n2oapp.framework.config.register.dynamic.JavaSourceLoader;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ConfigDynamicProviderCompileTest extends SourceCompileTestBase {

    private ApplicationRestService applicationRestService;

    @Override
    @Before
    public void setUp() throws Exception {
        applicationRestService = mock(ApplicationRestService.class);
        ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setName("appName");
        when(applicationRestService.getApplication(any())).thenReturn(applicationResponse);
        when(applicationRestService.getGroupedApplicationConfig(any())).thenReturn(GroupedApplicationConfigBuilder.build());

        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);

        ConfigDynamicProvider configDynamicProvider = new ConfigDynamicProvider();
        configDynamicProvider.setApplicationRestService(applicationRestService);

        builder.packs(new N2oAllDataPack(), new N2oAllPagesPack())
                .sources(new JavaInfo("configDynamic", N2oPage.class),
                        new CompileInfo("ru/i_novus/config/web/provider/groupedConfig.object.xml"))
                .providers(configDynamicProvider)
                .loaders(new JavaSourceLoader(builder.getEnvironment().getDynamicMetadataProviderFactory()));
    }

    @Test
    public void testDynamicPage() {
        SimplePage page = (SimplePage) compile("/ru/i_novus/config/web/provider/configDynamicObject.page.xml")
                .get(new PageContext("configDynamicObject", "/"));
        Widget widget = page.getWidget();
        assertThat(widget, instanceOf(Table.class));
        Table table = (Table) widget;
        assertThat(table.getComponent().getCells().size(), is(2));
        assertThat(table.getComponent().getCells().get(0).getId(), is("codeStr"));
        assertThat(table.getComponent().getCells().get(1).getId(), is("name"));

        // динамическая страница
        StandardPage dynamicPage = (StandardPage)routeAndGet("/systems/appCode/update", Page.class);
        assertThat(dynamicPage.getId(), is("systems_appCode_update"));
        assertThat(dynamicPage.getPageProperty().getTitle(), is("appName (appCode)"));
        assertThat(dynamicPage.getObject().getId(), is("groupedConfig"));

        // проверка филдсетов
        String widgetId = "systems_appCode_update_groupedConfigForm";
        Form form = (Form) dynamicPage.getWidgets().get(widgetId);
        List<FieldSet> fieldsets = form.getComponent().getFieldsets();
        assertThat(fieldsets.size(), is(3));
        // первый филдсет
        assertThat(fieldsets.get(0), instanceOf(LineFieldSet.class));
        LineFieldSet lineFieldSet = (LineFieldSet) fieldsets.get(0);
        assertThat(lineFieldSet.getLabel(), is("group1"));
        assertLineFieldSetProperties(lineFieldSet);
        assertThat(lineFieldSet.getRows().size(), is(2));

        StandardField field = (StandardField) lineFieldSet.getRows().get(0).getCols().get(0).getFields().get(0);
        assertThat(field.getControl(), instanceOf(InputText.class));
        assertThat(field.getControl().getSrc(), is("InputText"));
        assertThat(((InputText) field.getControl()).getPlaceholder(), is("default"));
        assertThat(field.getId(), is("data.test@code1"));
        assertThat(field.getLabel(), is("name1"));
        assertThat(field.getDescription(), is("test.code1"));
        assertThat(field.getHelp(), is("desc1"));
        assertThat(dynamicPage.getModels().get(String.format("resolve['%s'].%s", widgetId, field.getId())).getValue(), is("text"));

        field = (StandardField) lineFieldSet.getRows().get(1).getCols().get(0).getFields().get(0);
        assertThat(field.getControl(), instanceOf(InputText.class));
        assertThat(field.getControl().getSrc(), is("InputNumber"));
        assertThat(field.getId(), is("data.test@code2"));
        assertThat(field.getLabel(), is("name2"));
        assertThat(field.getDescription(), is("test.code2"));
        assertThat(field.getHelp(), is("desc2"));
        assertThat(dynamicPage.getModels().get(String.format("resolve['%s'].%s", widgetId, field.getId())).getValue(), is(123));

        // второй филдсет
        lineFieldSet = (LineFieldSet) fieldsets.get(1);
        assertThat(lineFieldSet.getLabel(), is("group2"));
        assertLineFieldSetProperties(lineFieldSet);
        assertThat(lineFieldSet.getRows().size(), is(2));
        field = (StandardField) lineFieldSet.getRows().get(0).getCols().get(0).getFields().get(0);
        assertThat(field.getControl(), instanceOf(Checkbox.class));
        assertThat(((Checkbox)field.getControl()).getLabel(), is("name3"));
        assertThat(field.getId(), is("data.test@code3"));
        assertThat(field.getDescription(), is("test.code3"));
        assertThat(field.getHelp(), is("desc3"));
        assertThat(dynamicPage.getModels().get(String.format("resolve['%s'].%s", widgetId, field.getId())).getValue(), is(true));

        field = (StandardField) lineFieldSet.getRows().get(1).getCols().get(0).getFields().get(0);
        assertThat(field.getControl(), instanceOf(Checkbox.class));
        assertThat(((Checkbox)field.getControl()).getLabel(), is("name4"));
        assertThat(field.getId(), is("data.test@code4"));
        assertThat(field.getDescription(), is("test.code4"));
        assertThat(field.getHelp(), is("desc4"));
        assertThat(dynamicPage.getModels().get(String.format("resolve['%s'].%s", widgetId, field.getId())).getValue(), is(false));

        // третий филдсет (скрытый)
        FieldSet fieldSet = fieldsets.get(2);
        assertThat(fieldSet.getRows().size(), is(1));
        field = (StandardField) fieldSet.getRows().get(0).getCols().get(0).getFields().get(0);
        assertThat(field.getControl(), instanceOf(Hidden.class));
        assertThat(field.getId(), is("appCode"));
        assertThat(field.getVisible(), is(false));
        assertThat(dynamicPage.getModels().get(String.format("resolve['%s'].%s", widgetId, field.getId())).getValue(), is("appCode"));


        // кнопки
        assertThat(dynamicPage.getToolbar().getGroup(0).getId(), is("bottomRight0"));
        List<AbstractButton> buttons = dynamicPage.getToolbar().getGroup(0).getButtons();
        assertThat(buttons.size(), is(2));
        AbstractButton button = buttons.get(0);
        assertThat(button.getId(), is("save"));
        assertThat(button.getColor(), is("primary"));
        assertThat(button.getLabel(), is("Сохранить"));
        assertThat(((InvokeAction)button.getAction()).getOperationId(), is("save"));
        button = buttons.get(1);
        assertThat(button.getId(), is("cancel"));
        assertThat(button.getLabel(), is("Отмена"));
        assertThat(((PerformButton)button).getUrl(), is("/systems/appCode"));

        // действия (actions)
        Map<String, Action> actions = dynamicPage.getActions();
        assertThat(actions.size(), is(2));
        assertThat(actions.get("save"), instanceOf(InvokeAction.class));
        // save
        InvokeAction action = (InvokeAction) actions.get("save");
        assertThat(action.getPayload().getPageId(), is("systems_appCode_update"));
        assertThat(action.getMeta(), notNullValue());
        assertThat(action.getObjectId(), is("groupedConfig"));
        assertThat(action.getOperationId(), is("save"));
        // cancel
        LinkActionImpl linkAction = (LinkActionImpl) actions.get("cancel");
        assertThat(linkAction.getUrl(), is("/systems/appCode"));
        assertThat(linkAction.getTarget(), is(Target.application));
        assertThat(linkAction.getObjectId(), nullValue());
        assertThat(linkAction.getOperationId(), nullValue());
        assertThat(linkAction.getPageId(), nullValue());
    }

    private void assertLineFieldSetProperties(LineFieldSet lineFieldSet) {
        assertThat(lineFieldSet.getCollapsible(), is(true));
        assertThat(lineFieldSet.getLabelPosition(), is(FieldSet.LabelPosition.LEFT));
        assertThat(lineFieldSet.getLabelAlignment(), is(FieldSet.LabelAlignment.LEFT));
        assertThat(lineFieldSet.getLabelWidth(), is("50%"));
    }
}