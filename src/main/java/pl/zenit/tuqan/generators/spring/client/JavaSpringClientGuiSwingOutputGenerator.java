package pl.zenit.tuqan.generators.spring.client;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.generators.literals.java.JavaScope;

public class JavaSpringClientGuiSwingOutputGenerator extends JavaOutputGenerator {

 public JavaSpringClientGuiSwingOutputGenerator(TuqanExecutionParameters params) {
            super(params);
      }
      
      @Override public void createOutput(TuqanObject data) throws GenerationFuckup {
            if (data instanceof TuqanScopeclass)
                  output(new JavaScope(new Upcaster(data).asInferred()));
            else 
            throw new GenerationFuckup("unknown operation");
            
      }
      
      private void output(JavaScope scope) throws GenerationFuckup {
            addPackageDeclaration(text, scope);
            addImports(text, scope);
            
            text.add("public class "+ scope.swingGui().displayFrameName() +" extends javax.swing.JFrame {");
            text.add("");
            addFields(text, scope);
            addConstructor(text, scope);
            addEventFunctions(text, scope);
            addComponentInit(text, scope);
            addComponentActions(text, scope);
            addComponentDeclaration(text, scope);
            
            File filename = getFileFor(scope.spring().crudGuiName() + ".java");
            File formFilename = getFileFor(scope.spring().crudGuiName()+ ".form");
            FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
            try {
                  saveFormFile(formFilename);
            }
            catch (IOException ex) {
                  throw new GenerationFuckup(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
      }

      private void addConstructor(IndentedStringList text, JavaScope scope) {
            text.add("public "+ scope.swingGui().displayFrameName() +"() {");
            text.pushIndent();
            text.add("initComponents();");
            text.popIndent();
            text.add("}");
            text.add("");
      }
      
    private void addPackageDeclaration(IndentedStringList text, JavaScope scope) {
        String pkg = new PackageLocationResolver(params).getRootPacakge() + ".dao";
        text.add("package " + pkg + ";");
        text.add("");
    }
    
      private void addImports(IndentedStringList text, JavaScope scope) {
            new ImportOrganizer().getCommonImports().forEach(i-> text.add("import " + i + ";"));
            text.add("import javax.swing.DefaultListModel;");
            text.add("import pl.zenit.brokerclient.model.Currency;");
            text.add("");
      }
      private void addFields(IndentedStringList text, JavaScope scope) {
            text.add("private final " + scope.spring().daoName() + " dao = new " + scope.spring().daoName() + "();");
            text.add("public final Function<" + scope.dtoName() + ", String> itemPrinter = item->");
            text.pushIndent();
            String line = scope.fields().stream()
                  .map(field-> "String.valueOf(item." + field.methodGetterName() + "())")
                  .collect(Collectors.joining(" + \", \" + "));
            text.add(line + ";");
            text.popIndent();
            text.add("private List<" + scope.dtoName() + "> data = new ArrayList<>();");
            text.add("");
      }
      private void addEventFunctions(IndentedStringList text, JavaScope scope) {
            text.add("private void getAll() {");
            text.pushIndent();
            text.add("data = dao." + scope.rest().methodGetAll() + "();");
            text.add("DefaultListModel<String> model = new DefaultListModel<>();");
            text.add("data.forEach(item-> model.addElement(itemPrinter.apply(item)));");
            text.add("jList1.setModel(model);");
            text.popIndent();
            text.add("}");
            text.add("");
            
            text.add("private void add() {");
            text.pushIndent();
            text.add("");
            text.popIndent();
            text.add("}");
            text.add("");
            
            text.add("private void edit() {");
            text.pushIndent();
            text.add("int index = getSelectedIndex();");
            text.add("if (index == -1)");
            text.pushIndent();
            text.add("return;");
            text.popIndent();
            text.add("Currency item = data.get(index);");
            text.popIndent();
            text.add("}");
            text.add("");
            
            text.add("private void del() {");
            text.pushIndent();
            text.add("int index = getSelectedIndex();");
            text.add("if (index == -1)");
            text.pushIndent();
            text.add("return;");
            text.popIndent();
            text.add("dao." + scope.rest().methodDelete() + "(index);");
            text.add("getAll();");
            text.popIndent();
            text.add("}");
            text.add("");
            
            text.add("private int getSelectedIndex() {");
            text.pushIndent();
            text.add("return jList1.getSelectedIndex();");
            text.popIndent();
            text.add("}");
            text.add("");
      }

      private void addComponentInit(IndentedStringList text, JavaScope scope) {
            text.add("@SuppressWarnings(\"unchecked\")");
            text.add("// <editor-fold" + " defaultstate=\"collapsed\" desc=\"Generated Code\">");
            text.add("private void initComponents() {");
            text.add("java.awt.GridBagConstraints gridBagConstraints;");
            text.add("");
            text.add("jButtonGetAll = new javax.swing.JButton();");
            text.add("filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(500, 0), new java.awt.Dimension(500, 0), new java.awt.Dimension(500, 32767));");
            text.add("filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 400), new java.awt.Dimension(0, 400), new java.awt.Dimension(32767, 400));");
            text.add("jScrollPane1 = new javax.swing.JScrollPane();");
            text.add("jList1 = new javax.swing.JList<>();");
            text.add("filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));");
            text.add("jButtonAdd = new javax.swing.JButton();");
            text.add("jButtonEdit = new javax.swing.JButton();");
            text.add("jButtonDelete = new javax.swing.JButton();");
            text.add("");
            text.add("setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);");
            text.add("getContentPane().setLayout(new java.awt.GridBagLayout());");
            text.add("");
            text.add("jButtonGetAll.setText(\"refresh\");");
            text.add("jButtonGetAll.addActionListener(new java.awt.event.ActionListener() {");
            text.pushIndent();
            text.add("public void actionPerformed(java.awt.event.ActionEvent evt) {");
            text.pushIndent();
            text.add("jButtonGetAllActionPerformed(evt);");
            text.popIndent();
            text.add("}");
            text.popIndent();
            text.add("});");
            text.add("gridBagConstraints = new java.awt.GridBagConstraints();");
            text.add("gridBagConstraints.gridx = 1;");
            text.add("gridBagConstraints.gridy = 1;");
            text.add("gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;");
            text.add("gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);");
            text.add("getContentPane().add(jButtonGetAll, gridBagConstraints);");
            text.add("gridBagConstraints = new java.awt.GridBagConstraints();");
            text.add("gridBagConstraints.gridx = 1;");
            text.add("gridBagConstraints.gridy = 0;");
            text.add("gridBagConstraints.gridwidth = 4;");
            text.add("gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);");
            text.add("getContentPane().add(filler1, gridBagConstraints);");
            text.add("gridBagConstraints = new java.awt.GridBagConstraints();");
            text.add("gridBagConstraints.gridx = 0;");
            text.add("gridBagConstraints.gridy = 2;");
            text.add("gridBagConstraints.gridheight = 4;");
            text.add("gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);");
            text.add("getContentPane().add(filler2, gridBagConstraints);");
            text.add("");
            text.add("jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);");
            text.add("");
            text.add("jList1.setModel(new javax.swing.AbstractListModel<String>() {");
            text.pushIndent();
            text.add("String[] strings = { \"Item 1\", \"Item 5\" };");
            text.add("public int getSize() { return strings.length; }");
            text.add("public String getElementAt(int i) { return strings[i]; }");
            text.popIndent();
            text.add("});");
            text.add("jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);");
            text.add("jList1.addMouseListener(new java.awt.event.MouseAdapter() {");
            text.pushIndent();            
            text.add("public void mousePressed(java.awt.event.MouseEvent evt) {");
            text.pushIndent();
            text.add("jList1MousePressed(evt);");
            text.popIndent();
            text.add("}");
            text.popIndent();
            text.add("});");
            text.add("jScrollPane1.setViewportView(jList1);");
            text.add("");
            text.add("gridBagConstraints = new java.awt.GridBagConstraints();");
            text.add("gridBagConstraints.gridx = 1;");
            text.add("gridBagConstraints.gridy = 2;");
            text.add("gridBagConstraints.gridwidth = 4;");
            text.add("gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;");
            text.add("gridBagConstraints.weightx = 1.0;");
            text.add("gridBagConstraints.weighty = 1.0;");
            text.add("gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);");
            text.add("getContentPane().add(jScrollPane1, gridBagConstraints);");
            text.add("gridBagConstraints = new java.awt.GridBagConstraints();");
            text.add("gridBagConstraints.gridx = 5;");
            text.add("gridBagConstraints.gridy = 5;");
            text.add("gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);");
            text.add("getContentPane().add(filler3, gridBagConstraints);");
            text.add("");
            text.add("jButtonAdd.setText(\"add\");");
            text.add("jButtonAdd.addActionListener(new java.awt.event.ActionListener() {");
            text.pushIndent();
            text.add("public void actionPerformed(java.awt.event.ActionEvent evt) {");
            text.pushIndent();
            text.add("jButtonAddActionPerformed(evt);");
            text.popIndent();
            text.add("}");
            text.popIndent();
            text.add("});");
            text.add("gridBagConstraints = new java.awt.GridBagConstraints();");
            text.add("gridBagConstraints.gridx = 2;");
            text.add("gridBagConstraints.gridy = 1;");
            text.add("gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;");
            text.add("gridBagConstraints.weightx = 1.0;");
            text.add("gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);");
            text.add("getContentPane().add(jButtonAdd, gridBagConstraints);");
            text.add("");
            text.add("jButtonEdit.setText(\"edit\");");
            text.add("jButtonEdit.addActionListener(new java.awt.event.ActionListener() {");
            text.pushIndent();
            text.add("public void actionPerformed(java.awt.event.ActionEvent evt) {");
            text.pushIndent();
            text.add("jButtonEditActionPerformed(evt);");
            text.popIndent();
            text.add("}");
            text.popIndent();
            text.add("});");
            text.add("gridBagConstraints = new java.awt.GridBagConstraints();");
            text.add("gridBagConstraints.gridx = 3;");
            text.add("gridBagConstraints.gridy = 1;");
            text.add("gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;");
            text.add("gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);");
            text.add("getContentPane().add(jButtonEdit, gridBagConstraints);");
            text.add("");
            text.add("jButtonDelete.setText(\"del\");");
            text.add("jButtonDelete.addActionListener(new java.awt.event.ActionListener() {");
            text.pushIndent();
            text.add("public void actionPerformed(java.awt.event.ActionEvent evt) {");
            text.pushIndent();
            text.add("jButtonDeleteActionPerformed(evt);");
            text.popIndent();
            text.add("}");
            text.popIndent();
            text.add("});");
            text.add("gridBagConstraints = new java.awt.GridBagConstraints();");
            text.add("gridBagConstraints.gridx = 4;");
            text.add("gridBagConstraints.gridy = 1;");
            text.add("gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;");
            text.add("gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);");
            text.add("getContentPane().add(jButtonDelete, gridBagConstraints);");
            text.add("");
            text.add("pack();");
            text.add("}// </editor" + "-fold>");
      }
      
      private void addComponentActions(IndentedStringList text, JavaScope scope) {
            text.add("private void jButtonGetAllActionPerformed(java.awt.event.ActionEvent evt) {");
            text.pushIndent();
            text.add("new Thread(this::getAll).start();");
            text.popIndent();
            text.add("}");
            text.add("");
            text.add("private void jList1MousePressed(java.awt.event.MouseEvent evt) {");
            text.pushIndent();
            text.add("if (evt.getClickCount() == 2)");
            text.add("if (evt.getButton() == 1)");
            text.add("new Thread(this::edit).start();");
            text.popIndent();
            text.add("}");
            text.add("");
            text.add("private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {");
            text.pushIndent();
            text.add("new Thread(this::del).start();");
            text.popIndent();
            text.add("}");
            text.add("");
            text.add("private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {");
            text.pushIndent();
            text.add("new Thread(this::add).start();");
            text.popIndent();
            text.add("}");
            text.add("");
            text.add("private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {");
            text.pushIndent();
            text.add("new Thread(this::edit).start();");
            text.popIndent();
            text.add("}");
            text.add("");
      }

      private void addComponentDeclaration(IndentedStringList text, JavaScope scope) {
            text.add("// <editor-fold defaultstate=\"collapsed\" desc=\"variable declaration\">");
            text.add("// Variables declaration - do not modify");
            text.add("private javax.swing.Box.Filler filler1;");
            text.add("private javax.swing.Box.Filler filler2;");
            text.add("private javax.swing.Box.Filler filler3;");
            text.add("private javax.swing.JButton jButtonAdd;");
            text.add("private javax.swing.JButton jButtonDelete;");
            text.add("private javax.swing.JButton jButtonEdit;");
            text.add("private javax.swing.JButton jButtonGetAll;");
            text.add("private javax.swing.JList<String> jList1;");
            text.add("private javax.swing.JScrollPane jScrollPane1;");
            text.add("// End of variables declaration");
            text.add("// </ " + "editor-fold>"); // dwa stringi bo się zwija netbeans pierdolony xD
      }

      private void saveFormFile(File file) throws IOException {
            List<String> sl = new ArrayList<>();
            sl.add("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            sl.add("");
            sl.add("<Form version=\"1.3\" maxVersion=\"1.9\" type=\"org.netbeans.modules.form.forminfo.JFrameFormInfo\">");
            sl.add("  <Properties>");
            sl.add("    <Property name=\"defaultCloseOperation\" type=\"int\" value=\"3\"/>");
            sl.add("  </Properties>");
            sl.add("  <SyntheticProperties>");
            sl.add("    <SyntheticProperty name=\"formSizePolicy\" type=\"int\" value=\"1\"/>");
            sl.add("    <SyntheticProperty name=\"generateCenter\" type=\"boolean\" value=\"false\"/>");
            sl.add("  </SyntheticProperties>");
            sl.add("  <AuxValues>");
            sl.add("    <AuxValue name=\"FormSettings_autoResourcing\" type=\"java.lang.Integer\" value=\"0\"/>");
            sl.add("    <AuxValue name=\"FormSettings_autoSetComponentName\" type=\"java.lang.Boolean\" value=\"false\"/>");
            sl.add("    <AuxValue name=\"FormSettings_generateFQN\" type=\"java.lang.Boolean\" value=\"true\"/>");
            sl.add("    <AuxValue name=\"FormSettings_generateMnemonicsCode\" type=\"java.lang.Boolean\" value=\"false\"/>");
            sl.add("    <AuxValue name=\"FormSettings_i18nAutoMode\" type=\"java.lang.Boolean\" value=\"false\"/>");
            sl.add("    <AuxValue name=\"FormSettings_layoutCodeTarget\" type=\"java.lang.Integer\" value=\"1\"/>");
            sl.add("    <AuxValue name=\"FormSettings_listenerGenerationStyle\" type=\"java.lang.Integer\" value=\"0\"/>");
            sl.add("    <AuxValue name=\"FormSettings_variablesLocal\" type=\"java.lang.Boolean\" value=\"false\"/>");
            sl.add("    <AuxValue name=\"FormSettings_variablesModifier\" type=\"java.lang.Integer\" value=\"2\"/>");
            sl.add("    <AuxValue name=\"designerSize\" type=\"java.awt.Dimension\" value=\"-84,-19,0,5,115,114,0,18,106,97,118,97,46,97,119,116,46,68,105,109,101,110,115,105,111,110,65,-114,-39,-41,-84,95,68,20,2,0,2,73,0,6,104,101,105,103,104,116,73,0,5,119,105,100,116,104,120,112,0,0,1,-71,0,0,1,-9\"/>");
            sl.add("  </AuxValues>");
            sl.add("");
            sl.add("  <Layout class=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout\"/>");
            sl.add("  <SubComponents>");
            sl.add("    <Component class=\"javax.swing.JButton\" name=\"jButtonGetAll\">");
            sl.add("      <Properties>");
            sl.add("        <Property name=\"text\" type=\"java.lang.String\" value=\"refresh\"/>");
            sl.add("      </Properties>");
            sl.add("      <Events>");
            sl.add("        <EventHandler event=\"actionPerformed\" listener=\"java.awt.event.ActionListener\" parameters=\"java.awt.event.ActionEvent\" handler=\"jButtonGetAllActionPerformed\"/>");
            sl.add("      </Events>");
            sl.add("      <Constraints>");
            sl.add("        <Constraint layoutClass=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout\" value=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout$GridBagConstraintsDescription\">");
            sl.add("          <GridBagConstraints gridX=\"1\" gridY=\"1\" gridWidth=\"1\" gridHeight=\"1\" fill=\"0\" ipadX=\"0\" ipadY=\"0\" insetsTop=\"5\" insetsLeft=\"5\" insetsBottom=\"5\" insetsRight=\"5\" anchor=\"21\" weightX=\"0.0\" weightY=\"0.0\"/>");
            sl.add("        </Constraint>");
            sl.add("      </Constraints>");
            sl.add("    </Component>");
            sl.add("    <Component class=\"javax.swing.Box$Filler\" name=\"filler1\">");
            sl.add("      <Properties>");
            sl.add("        <Property name=\"maximumSize\" type=\"java.awt.Dimension\" editor=\"org.netbeans.beaninfo.editors.DimensionEditor\">");
            sl.add("          <Dimension value=\"[500, 32767]\"/>");
            sl.add("        </Property>");
            sl.add("        <Property name=\"minimumSize\" type=\"java.awt.Dimension\" editor=\"org.netbeans.beaninfo.editors.DimensionEditor\">");
            sl.add("          <Dimension value=\"[500, 0]\"/>");
            sl.add("        </Property>");
            sl.add("        <Property name=\"preferredSize\" type=\"java.awt.Dimension\" editor=\"org.netbeans.beaninfo.editors.DimensionEditor\">");
            sl.add("          <Dimension value=\"[500, 0]\"/>");
            sl.add("        </Property>");
            sl.add("      </Properties>");
            sl.add("      <AuxValues>");
            sl.add("        <AuxValue name=\"classDetails\" type=\"java.lang.String\" value=\"Box.Filler.HorizontalStrut\"/>");
            sl.add("      </AuxValues>");
            sl.add("      <Constraints>");
            sl.add("        <Constraint layoutClass=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout\" value=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout$GridBagConstraintsDescription\">");
            sl.add("          <GridBagConstraints gridX=\"1\" gridY=\"0\" gridWidth=\"4\" gridHeight=\"1\" fill=\"0\" ipadX=\"0\" ipadY=\"0\" insetsTop=\"5\" insetsLeft=\"5\" insetsBottom=\"5\" insetsRight=\"5\" anchor=\"10\" weightX=\"0.0\" weightY=\"0.0\"/>");
            sl.add("        </Constraint>");
            sl.add("      </Constraints>");
            sl.add("    </Component>");
            sl.add("    <Component class=\"javax.swing.Box$Filler\" name=\"filler2\">");
            sl.add("      <Properties>");
            sl.add("        <Property name=\"maximumSize\" type=\"java.awt.Dimension\" editor=\"org.netbeans.beaninfo.editors.DimensionEditor\">");
            sl.add("          <Dimension value=\"[32767, 400]\"/>");
            sl.add("        </Property>");
            sl.add("        <Property name=\"minimumSize\" type=\"java.awt.Dimension\" editor=\"org.netbeans.beaninfo.editors.DimensionEditor\">");
            sl.add("          <Dimension value=\"[0, 400]\"/>");
            sl.add("        </Property>");
            sl.add("        <Property name=\"preferredSize\" type=\"java.awt.Dimension\" editor=\"org.netbeans.beaninfo.editors.DimensionEditor\">");
            sl.add("          <Dimension value=\"[0, 400]\"/>");
            sl.add("        </Property>");
            sl.add("      </Properties>");
            sl.add("      <AuxValues>");
            sl.add("        <AuxValue name=\"classDetails\" type=\"java.lang.String\" value=\"Box.Filler.VerticalStrut\"/>");
            sl.add("      </AuxValues>");
            sl.add("      <Constraints>");
            sl.add("        <Constraint layoutClass=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout\" value=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout$GridBagConstraintsDescription\">");
            sl.add("          <GridBagConstraints gridX=\"0\" gridY=\"2\" gridWidth=\"1\" gridHeight=\"4\" fill=\"0\" ipadX=\"0\" ipadY=\"0\" insetsTop=\"5\" insetsLeft=\"5\" insetsBottom=\"5\" insetsRight=\"5\" anchor=\"10\" weightX=\"0.0\" weightY=\"0.0\"/>");
            sl.add("        </Constraint>");
            sl.add("      </Constraints>");
            sl.add("    </Component>");
            sl.add("    <Container class=\"javax.swing.JScrollPane\" name=\"jScrollPane1\">");
            sl.add("      <Properties>");
            sl.add("        <Property name=\"verticalScrollBarPolicy\" type=\"int\" value=\"22\"/>");
            sl.add("      </Properties>");
            sl.add("      <AuxValues>");
            sl.add("        <AuxValue name=\"autoScrollPane\" type=\"java.lang.Boolean\" value=\"true\"/>");
            sl.add("      </AuxValues>");
            sl.add("      <Constraints>");
            sl.add("        <Constraint layoutClass=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout\" value=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout$GridBagConstraintsDescription\">");
            sl.add("          <GridBagConstraints gridX=\"1\" gridY=\"2\" gridWidth=\"4\" gridHeight=\"1\" fill=\"1\" ipadX=\"0\" ipadY=\"0\" insetsTop=\"5\" insetsLeft=\"5\" insetsBottom=\"5\" insetsRight=\"5\" anchor=\"10\" weightX=\"1.0\" weightY=\"1.0\"/>");
            sl.add("        </Constraint>");
            sl.add("      </Constraints>");
            sl.add("");
            sl.add("      <Layout class=\"org.netbeans.modules.form.compat2.layouts.support.JScrollPaneSupportLayout\"/>");
            sl.add("      <SubComponents>");
            sl.add("        <Component class=\"javax.swing.JList\" name=\"jList1\">");
            sl.add("          <Properties>");
            sl.add("            <Property name=\"model\" type=\"javax.swing.ListModel\" editor=\"org.netbeans.modules.form.editors2.ListModelEditor\">");
            sl.add("              <StringArray count=\"5\">");
            sl.add("                <StringItem index=\"0\" value=\"Item 1\"/>");
            sl.add("                <StringItem index=\"4\" value=\"Item 5\"/>");
            sl.add("              </StringArray>");
            sl.add("            </Property>");
            sl.add("            <Property name=\"selectionMode\" type=\"int\" value=\"0\"/>");
            sl.add("          </Properties>");
            sl.add("          <Events>");
            sl.add("            <EventHandler event=\"mousePressed\" listener=\"java.awt.event.MouseListener\" parameters=\"java.awt.event.MouseEvent\" handler=\"jList1MousePressed\"/>");
            sl.add("          </Events>");
            sl.add("          <AuxValues>");
            sl.add("            <AuxValue name=\"JavaCodeGenerator_TypeParameters\" type=\"java.lang.String\" value=\"&lt;String&gt;\"/>");
            sl.add("          </AuxValues>");
            sl.add("        </Component>");
            sl.add("      </SubComponents>");
            sl.add("    </Container>");
            sl.add("    <Component class=\"javax.swing.Box$Filler\" name=\"filler3\">");
            sl.add("      <AuxValues>");
            sl.add("        <AuxValue name=\"classDetails\" type=\"java.lang.String\" value=\"Box.Filler.RigidArea\"/>");
            sl.add("      </AuxValues>");
            sl.add("      <Constraints>");
            sl.add("        <Constraint layoutClass=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout\" value=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout$GridBagConstraintsDescription\">");
            sl.add("          <GridBagConstraints gridX=\"5\" gridY=\"5\" gridWidth=\"1\" gridHeight=\"1\" fill=\"0\" ipadX=\"0\" ipadY=\"0\" insetsTop=\"5\" insetsLeft=\"5\" insetsBottom=\"5\" insetsRight=\"5\" anchor=\"10\" weightX=\"0.0\" weightY=\"0.0\"/>");
            sl.add("        </Constraint>");
            sl.add("      </Constraints>");
            sl.add("    </Component>");
            sl.add("    <Component class=\"javax.swing.JButton\" name=\"jButtonAdd\">");
            sl.add("      <Properties>");
            sl.add("        <Property name=\"text\" type=\"java.lang.String\" value=\"add\"/>");
            sl.add("      </Properties>");
            sl.add("      <Events>");
            sl.add("        <EventHandler event=\"actionPerformed\" listener=\"java.awt.event.ActionListener\" parameters=\"java.awt.event.ActionEvent\" handler=\"jButtonAddActionPerformed\"/>");
            sl.add("      </Events>");
            sl.add("      <Constraints>");
            sl.add("        <Constraint layoutClass=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout\" value=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout$GridBagConstraintsDescription\">");
            sl.add("          <GridBagConstraints gridX=\"2\" gridY=\"1\" gridWidth=\"1\" gridHeight=\"1\" fill=\"0\" ipadX=\"0\" ipadY=\"0\" insetsTop=\"5\" insetsLeft=\"5\" insetsBottom=\"5\" insetsRight=\"5\" anchor=\"22\" weightX=\"1.0\" weightY=\"0.0\"/>");
            sl.add("        </Constraint>");
            sl.add("      </Constraints>");
            sl.add("    </Component>");
            sl.add("    <Component class=\"javax.swing.JButton\" name=\"jButtonEdit\">");
            sl.add("      <Properties>");
            sl.add("        <Property name=\"text\" type=\"java.lang.String\" value=\"edit\"/>");
            sl.add("      </Properties>");
            sl.add("      <Events>");
            sl.add("        <EventHandler event=\"actionPerformed\" listener=\"java.awt.event.ActionListener\" parameters=\"java.awt.event.ActionEvent\" handler=\"jButtonEditActionPerformed\"/>");
            sl.add("      </Events>");
            sl.add("      <Constraints>");
            sl.add("        <Constraint layoutClass=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout\" value=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout$GridBagConstraintsDescription\">");
            sl.add("          <GridBagConstraints gridX=\"3\" gridY=\"1\" gridWidth=\"1\" gridHeight=\"1\" fill=\"0\" ipadX=\"0\" ipadY=\"0\" insetsTop=\"5\" insetsLeft=\"5\" insetsBottom=\"5\" insetsRight=\"5\" anchor=\"22\" weightX=\"0.0\" weightY=\"0.0\"/>");
            sl.add("        </Constraint>");
            sl.add("      </Constraints>");
            sl.add("    </Component>");
            sl.add("    <Component class=\"javax.swing.JButton\" name=\"jButtonDelete\">");
            sl.add("      <Properties>");
            sl.add("        <Property name=\"text\" type=\"java.lang.String\" value=\"del\"/>");
            sl.add("      </Properties>");
            sl.add("      <Events>");
            sl.add("        <EventHandler event=\"actionPerformed\" listener=\"java.awt.event.ActionListener\" parameters=\"java.awt.event.ActionEvent\" handler=\"jButtonDeleteActionPerformed\"/>");
            sl.add("      </Events>");
            sl.add("      <Constraints>");
            sl.add("        <Constraint layoutClass=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout\" value=\"org.netbeans.modules.form.compat2.layouts.DesignGridBagLayout$GridBagConstraintsDescription\">");
            sl.add("          <GridBagConstraints gridX=\"4\" gridY=\"1\" gridWidth=\"1\" gridHeight=\"1\" fill=\"0\" ipadX=\"0\" ipadY=\"0\" insetsTop=\"5\" insetsLeft=\"5\" insetsBottom=\"5\" insetsRight=\"5\" anchor=\"22\" weightX=\"0.0\" weightY=\"0.0\"/>");
            sl.add("        </Constraint>");
            sl.add("      </Constraints>");
            sl.add("    </Component>");
            sl.add("  </SubComponents>");
            sl.add("</Form>");
            if (file.exists() && file.isFile())
                  Files.delete(file.toPath());
            Files.write(file.toPath(), sl);
      }
      
    @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File clientDir = plr.getSpringDesktopClientProjectRoot(overallRoot);
        clientDir = plr.getSourceCodeAppRootDir(clientDir);
        clientDir = new File(clientDir, "dao");
        return new File(clientDir, filename);
    }
    
}