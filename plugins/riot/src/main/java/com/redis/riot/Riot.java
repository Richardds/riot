package com.redis.riot;

import com.redis.riot.core.MainCommand;
import com.redis.riot.operation.OperationCommand;
import com.redis.spring.batch.item.redis.common.Range;

import io.lettuce.core.RedisURI;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExecutionStrategy;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.RunFirst;
import picocli.CommandLine.RunLast;
import software.amazon.awssdk.regions.Region;

@Command(name = "riot", versionProvider = Versions.class, headerHeading = "A data import/export tool for Redis.%n%n", footerHeading = "%nRun 'riot COMMAND --help' for more information on a command.%n%nFor more help on how to use RIOT, head to http://redis.github.io/riot%n", subcommands = {
		DatabaseExport.class, DatabaseImport.class, FakerImport.class, FileExport.class, FileImport.class,
		Generate.class, Ping.class, Replicate.class, Compare.class, GenerateCompletion.class })
public class Riot extends MainCommand {

	public static void main(String[] args) {
		System.exit(new Riot().run(args));
	}

	@Override
	protected CommandLine commandLine() {
		CommandLine commandLine = super.commandLine();
		commandLine.registerConverter(RedisURI.class, new RedisURIConverter());
		commandLine.registerConverter(Region.class, Region::of);
		commandLine.registerConverter(Range.class, new RangeConverter());
		return commandLine;
	}

	@Override
	protected IExecutionStrategy executionStrategy() {
		return Riot::executionStrategy;
	}

	public static int executionStrategy(ParseResult parseResult) {
		for (ParseResult subcommand : parseResult.subcommands()) {
			Object command = subcommand.commandSpec().userObject();
			if (AbstractImportCommand.class.isAssignableFrom(command.getClass())) {
				AbstractImportCommand importCommand = (AbstractImportCommand) command;
				for (ParseResult redisCommand : subcommand.subcommands()) {
					if (redisCommand.isUsageHelpRequested()) {
						return new RunLast().execute(redisCommand);
					}
					importCommand.getImportOperationCommands()
							.add((OperationCommand) redisCommand.commandSpec().userObject());
				}
				return new RunFirst().execute(subcommand);
			}
		}
		return new RunLast().execute(parseResult); // default execution strategy
	}

}
