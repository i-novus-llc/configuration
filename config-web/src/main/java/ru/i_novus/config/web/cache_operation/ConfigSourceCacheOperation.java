package ru.i_novus.config.web.cache_operation;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.metadata.SourceMetadata;
import net.n2oapp.framework.api.metadata.compile.BindProcessor;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.validate.ValidateProcessor;
import net.n2oapp.framework.config.compile.pipeline.operation.SourceCacheOperation;

import java.util.function.Supplier;

public class ConfigSourceCacheOperation extends SourceCacheOperation {

    @Override
    public SourceMetadata execute(CompileContext context, DataSet data, Supplier supplier, CompileProcessor compileProcessor,
                                  BindProcessor bindProcessor, ValidateProcessor validateProcessor) {
        String sourceId = context.getSourceId(bindProcessor);
        return sourceId.startsWith("configDynamic") ?
                (SourceMetadata) supplier.get() :
                super.execute(context, data, supplier, compileProcessor, bindProcessor, validateProcessor);
    }
}
