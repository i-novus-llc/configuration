package ru.i_novus.config.web.provider;

import net.n2oapp.framework.api.metadata.global.view.page.N2oPage;
import net.n2oapp.framework.api.metadata.meta.Page;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.N2oAllDataPack;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.register.JavaInfo;
import net.n2oapp.framework.config.register.dynamic.JavaSourceLoader;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.i_novus.system_application.api.service.ApplicationRestService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ConfigDynamicProviderCompileTest extends SourceCompileTestBase {

    @MockBean
    private ApplicationRestService applicationRestService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        when(applicationRestService.getGroupedApplicationConfig(any()))
                .thenReturn(GroupedConfigRequestBuilder.build());
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllDataPack(), new N2oAllPagesPack())
                .sources(new JavaInfo("configDynamic", N2oPage.class),
                        new CompileInfo("ru/i_novus/config/web/provider/groupedConfigForm.widget.xml"))
                .providers(new ConfigDynamicProvider())
                .loaders(new JavaSourceLoader(builder.getEnvironment().getDynamicMetadataProviderFactory()));
    }

    @Test
    @Ignore
    public void testDynamicPage() {
        Page page = compile("net/n2oapp/framework/config/metadata/compile/dynamic/testDynamicObject.page.xml")
                .get(new PageContext("configDynamicObject", "/configDynamicObject"));
    }


}