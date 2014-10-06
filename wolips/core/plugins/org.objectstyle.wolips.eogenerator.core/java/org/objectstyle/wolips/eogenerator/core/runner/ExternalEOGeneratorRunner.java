package org.objectstyle.wolips.eogenerator.core.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.eogenerator.core.model.CommandLineTokenizer;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.IEOGeneratorRunner;

public class ExternalEOGeneratorRunner implements IEOGeneratorRunner {
	public boolean generate(EOGeneratorModel eogenModel, StringBuffer results, IProgressMonitor monitor) throws ParseException, IOException, InterruptedException {
		String eogenFileContents = eogenModel.writeToString(eogenModel.getProjectPath().toFile());
		List<String> commandsList = new LinkedList<String>();
		CommandLineTokenizer tokenizer = new CommandLineTokenizer(eogenFileContents);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if ("-extension".equals(token)) {
				tokenizer.nextToken();
			}
			else {
				commandsList.add(token);
			}
		}
		String[] tokens = commandsList.toArray(new String[commandsList.size()]);
		if (!new File(eogenModel.getEOGeneratorPath()).exists()) {
			throw new FileNotFoundException("You have either not set the path to your EOGenerator executable, or the current path is incorrect.");
		}
		IPath projectPath = eogenModel.getProjectPath();
		Process process = Runtime.getRuntime().exec(tokens, null, projectPath.toFile());

		InputStream inputstream = process.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

		int outputLines = 0;
		String line;
		while ((line = bufferedreader.readLine()) != null) {
			System.out.println("ExternalEOGeneratorRunner.generate: " + line);
			results.append(line);
			results.append('\n');
			outputLines++;
		}

		int errorCode = process.waitFor();
		if (errorCode != 0) {
			throw new RuntimeException("EOGenerator failed with code #" + errorCode + ".");
		}
		return false;
	}
}
