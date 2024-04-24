package j2b.convertToXml;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.sailpoint.sail4j.javaparser.RuleClassDefinition;
import com.sailpoint.sail4j.javaparser.RuleDefinition;
import com.sailpoint.sail4j.javaparser.RuleTransfomer;

/**
 * Utility class for converting Java files to XML using Sail4j and Apache Velocity.
 * <p>
 * This class provides methods to read Java files, parse them into rule definitions,
 * and generate XML files based on Velocity templates.
 * 
 * Library used :
 * Sail4j - https://github.com/renliangfeng/sail4j-iiq-idn
 * Apache Velocity Templates
 * </p>
 * <p>
 * Author: Abhishek Sinha
 * Version: 1.0
 * </p>
 */
public class Transformer {

    /**
     * Main method for executing the Java to XML transformation process.
     *
     * @param args Command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Define source and destination paths
        String sourcePath = "";

        //example 
        //String sourcePath = System.getProperty("user.dir") + "\\src\\j2b\\ruleDefinitions\\input\\";

        String destinationPath = "";

        //example
        //String destinationPath = System.getProperty("user.dir") + "\\src\\output";

        // Create a File object for the source directory
        File path = new File(sourcePath);

        // Get a list of files in the source directory
        File[] files = path.listFiles();

        // Iterate through each file in the directory
        for (File file : files) {
            // Process only Java files
            if (file.isFile() && file.getName().endsWith(".java")) {

                // Create a RuleClassDefinition instance
                RuleClassDefinition ruleClassDefinition = new RuleClassDefinition();

                // Create a RuleTransformer instance with the RuleClassDefinition
                RuleTransfomer ruleTransfomer = new RuleTransfomer(ruleClassDefinition);

                // Convert Java file to Rule XML
                RuleDefinition ruleDefinition = ruleTransfomer.convertJavaToRuleXml(file, ruleClassDefinition);

                // Initialize Velocity engine
                VelocityEngine ve = new VelocityEngine();
                ve.setProperty("resource.loader", "classpath");
                ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

                // Determine the template path based on rule type
                String templatePath = ruleDefinition.getRuleType() != null ?
                        "rule-templates/" + ruleDefinition.getRuleType() + ".vm" :
                        "rule-templates/GeneralRule.vm";

                try {
                    ve.init();

                    // Create a FileWriter for the destination XML file
                    Writer writer = new FileWriter(new File(destinationPath + File.pathSeparator + file.getName().replace(".java", ".xml")));

                    // Get the Velocity template
                    Template template = ve.getTemplate(templatePath, "UTF-8");

                    System.out.println("Rule name: " + ruleDefinition.getRuleName());

                    // Create a Velocity context and populate it with rule data
                    Context context = new VelocityContext();
                    context.put("ruleName", ruleDefinition.getRuleName());
                    context.put("ruleContent", ruleDefinition.getRuleContent());

                    // Merge the template with the context and write to the destination file
                    template.merge(context, writer);

                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
 