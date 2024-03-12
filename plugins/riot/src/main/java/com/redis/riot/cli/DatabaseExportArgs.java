package com.redis.riot.cli;

import java.util.regex.Pattern;

import com.redis.riot.core.AbstractMapExport;
import com.redis.riot.db.DatabaseExport;

import picocli.CommandLine.Option;

public class DatabaseExportArgs extends DatabaseArgs {

    @Option(names = "--key-regex", description = "Regex for key-field extraction (default: ${DEFAULT-VALUE}).", paramLabel = "<str>")
    Pattern keyRegex = AbstractMapExport.DEFAULT_KEY_REGEX;

    @Option(names = "--no-assert-updates", description = "Confirm every insert results in update of at least one row. True by default.", negatable = true)
    boolean assertUpdates = DatabaseExport.DEFAULT_ASSERT_UPDATES;

}
