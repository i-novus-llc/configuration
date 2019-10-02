package ru.i_novus.config.web.cache;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.metadata.compile.BindProcessor;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.validate.ValidateProcessor;
import net.n2oapp.framework.config.compile.pipeline.operation.CompileCacheOperation;

import java.util.function.Supplier;

public class ConfigCompileCacheOperation extends CompileCacheOperation {

    @Override
    public Object execute(CompileContext context, DataSet data, Supplier supplier, CompileProcessor compileProcessor,
                          BindProcessor bindProcessor, ValidateProcessor validateProcessor) {
        String key = context.getSourceId(bindProcessor);
        return (key.startsWith("configDynamic")) ?
                supplier.get() :
                super.execute(context, data, supplier, compileProcessor, bindProcessor, validateProcessor);
    }
}
