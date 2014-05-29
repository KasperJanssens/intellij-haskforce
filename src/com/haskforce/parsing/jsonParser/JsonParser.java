package com.haskforce.parsing.jsonParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haskforce.parsing.srcExtsDatatypes.*;
import com.haskforce.utils.ExecUtil;
import com.haskforce.utils.HaskellToolsNotificationListener;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ExceptionUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Parser responsible for parsing the JSON output from parser-helper.
 */
public class JsonParser {
    private static final String NOTIFICATION_TITLE = "Parser-helper notification";
    private static final Pattern cppPattern = Pattern.compile("^\\s*#(.*)$", Pattern.MULTILINE);
    private static final Logger LOG = Logger.getInstance(JsonParser.class);
    private final Project myProject;

    public JsonParser(@NotNull final Project project) {
        myProject = project;
    }

    /**
     * Parses the input by putting it in a file and calling parser-helper on
     * that file.
     */
    public TopPair parse(@NotNull CharSequence input) {
        TopPair tp = new TopPair();
        PropertiesComponent prop = PropertiesComponent.getInstance(myProject);
        String parserHelperPath = prop == null ? ExecUtil.locateExecutableByGuessing("parser-helper") : prop.getValue("parserHelperPath", "");

        if (parserHelperPath == null || parserHelperPath.isEmpty()) {
            Notifications.Bus.notify(
                    new Notification("Parser-helper", NOTIFICATION_TITLE,
                            "Parser-helper executable path is empty"+
                                    "<br/><a href='configureHaskellTools'>Configure</a>",
                            NotificationType.WARNING, new HaskellToolsNotificationListener(myProject)), myProject);
            tp.error = "Parser-helper unavailable.";
            return tp;
        }

        String json = getJson(input, parserHelperPath);
        if (json == null || json.startsWith("ERROR:")) {
            tp.error = json == null ? "Unable to execute Parser-helper." : json.substring("ERROR:".length());
            return tp;
        }

        Gson gson = createJSonDeserializer();
        return gson.fromJson(json, TopPair.class);
    }

    /**
     * Executes parser-helper and returns the result.
     */
    public String getJson(@NotNull CharSequence input, @NotNull String parserHelperPath) {
        final String bwPath = ".dist-buildwrapper";
        try {
            String fixedInput = cppPattern.matcher(input).replaceAll(" ");
            final File tmpFile = FileUtil.createTempFile(new File(bwPath), "ToParse", ".hs", true, true);
            FileUtil.appendToFile(tmpFile, fixedInput);

            // Construct command line for parser-helper.
            final String stuffToRun = parserHelperPath + ' ' + bwPath + File.separator + tmpFile.getName();

            // Run parser-helper.
            return ExecUtil.exec(stuffToRun);
        } catch (Exception ex) {
            Notifications.Bus.notify(new Notification("Parser2",
                    "Creating a temp file failed.", ExceptionUtil.getUserStackTrace(ex, LOG),
                    NotificationType.ERROR), myProject);
            LOG.error(ex);
        }
        return null;
    }

    /**
     * Create a finished JSON deserializer with all the necessary type adapters
     * already registered.
     */
    private static Gson createJSonDeserializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ModuleTopType.class, new ModuleTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(DeclTopType.class, new DeclTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(TypeTopType.class, new TypeTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(PatTopType.class, new PatTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(NameTopType.class, new NameTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(RhsTopType.class, new RhsTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(QNameTopType.class, new QNameTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(ExpTopType.class, new ExpTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(SpecialConTopType.class, new SpecialConTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(LiteralTopType.class, new LiteralTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(QOpTopType.class, new QOpTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(MatchTopType.class, new MatchTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(ImportSpecTopType.class, new ImportSpecTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(CNameTopType.class, new CNameTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(SafetyTopType.class, new SafetyTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(CallConvTopType.class, new CallConvTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(DataOrNewTopType.class, new DataOrNewTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(BangTypeTopType.class, new BangTypeTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(BoxedTopType.class, new BoxedTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(IPNameTopType.class, new IPNameTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(FieldUpdateTopType.class, new FieldUpdateTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(QualStmtTopType.class, new QualStmtTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(BracketTopType.class, new BracketTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(SpliceTopType.class, new SpliceTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(XNameTopType.class, new XNameTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(PromotedTopType.class, new PromotedTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(AssocTopType.class, new AssocTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(OpTopType.class, new OpTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(RuleVarTopType.class, new RuleVarTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(ActivationTopType.class, new ActivationTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(AnnotationTopType.class, new AnnotationTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(PatFieldTopType.class, new PatFieldTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(RPatTopType.class, new RPatTopTypeDeserializer());
        gsonBuilder.registerTypeAdapter(ExportSpecTopType.class, new ExportSpecTopTypeDeserializer());

        // Special case parsing for non-sum types.
        gsonBuilder.registerTypeAdapter(TopPair.class, new TopPairDeserializer());
        gsonBuilder.registerTypeAdapter(ModuleHead.class, new ModuleHeadDeserializer());
        gsonBuilder.registerTypeAdapter(ModuleName.class, new ModuleNameDeserializer());
        gsonBuilder.registerTypeAdapter(Comment.class, new CommentDeserializer());
        gsonBuilder.registerTypeAdapter(Deriving.class, new DerivingDeserializer());
        gsonBuilder.registerTypeAdapter(ImportDecl.class, new ImportDeclDeserializer());
        gsonBuilder.registerTypeAdapter(ImportSpecList.class, new ImportSpecListDeserializer());
        gsonBuilder.registerTypeAdapter(NameStringPair.class, new NameStringPairDeserializer());
        gsonBuilder.registerTypeAdapter(Alt.class, new AltDeserializer());
        gsonBuilder.registerTypeAdapter(GuardedAlt.class, new GuardedAltDeserializer());
        gsonBuilder.registerTypeAdapter(IfAlt.class, new IfAltDeserializer());
        gsonBuilder.registerTypeAdapter(XAttr.class, new XAttrDeserializer());
        gsonBuilder.registerTypeAdapter(GadtDecl.class, new GadtDeclDeserializer());
        gsonBuilder.registerTypeAdapter(FunDep.class, new FunDepDeserializer());
        gsonBuilder.registerTypeAdapter(Rule.class, new RuleDeserializer());
        gsonBuilder.registerTypeAdapter(PXAttr.class, new PXAttrDeserializer());

        Gson gson = gsonBuilder.setPrettyPrinting().create();
        return gson;
    }
}
