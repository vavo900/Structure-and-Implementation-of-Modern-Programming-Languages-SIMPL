/*
 * Decompiled with CFR 0.152.
 */
package edu.utexas.cs.sam.ui;

import edu.utexas.cs.sam.core.AssemblerException;
import edu.utexas.cs.sam.core.Memory;
import edu.utexas.cs.sam.core.Processor;
import edu.utexas.cs.sam.core.Program;
import edu.utexas.cs.sam.core.SamAssembler;
import edu.utexas.cs.sam.core.Sys;
import edu.utexas.cs.sam.core.SystemException;
import edu.utexas.cs.sam.core.Video;
import edu.utexas.cs.sam.utils.ProgramState;
import edu.utexas.cs.sam.utils.SamThread;
import edu.utexas.cs.sam.utils.XMLUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class TestScript {
    protected Sys sys;
    protected Processor proc;
    protected Memory mem;
    protected List<Test> tests = new ArrayList<Test>();
    protected File sourceFile = null;

    public TestScript() {
        this.sys = new Sys();
        this.proc = this.sys.cpu();
        this.mem = this.sys.mem();
    }

    public File getSourceFile() {
        return this.sourceFile;
    }

    public void setSourceFile(File file) {
        this.sourceFile = file;
    }

    public void load(InputStream toParse) throws TestScriptException {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(toParse);
        }
        catch (ParserConfigurationException e) {
            throw new TestScriptException("File Parse Error");
        }
        catch (IOException e) {
            throw new TestScriptException("File Parse Error");
        }
        catch (SAXException e) {
            throw new TestScriptException("File Parse Error");
        }
        NodeList l = doc.getElementsByTagName("testscript");
        if (l.getLength() != 1) {
            throw new TestScriptException("Invalid File");
        }
        Element root = (Element)l.item(0);
        if (root.getAttribute("version") == null || !root.getAttribute("version").equals("1.0")) {
            throw new TestScriptException("Incorrect Version in Test Script");
        }
        l = root.getElementsByTagName("test");
        int i = 0;
        while (i < l.getLength()) {
            this.tests.add(this.processTest((Element)l.item(i)));
            ++i;
        }
    }

    protected Test processTest(Element e) throws TestScriptException {
        String fileName = e.getAttribute("filename");
        if (fileName == null) {
            throw new TestScriptException("Invalid filename for test");
        }
        Test t = new Test(fileName);
        t.setScriptFile(this);
        NodeList l = e.getElementsByTagName("io");
        if (l.getLength() == 0) {
            throw new TestScriptException("Each test must have a return value");
        }
        int i = 0;
        while (i < l.getLength()) {
            Object data;
            String typeParam;
            String classParam;
            block38: {
                String dataString;
                block36: {
                    block37: {
                        Element io = (Element)l.item(i);
                        classParam = io.getAttribute("class");
                        typeParam = io.getAttribute("type");
                        if (classParam == null || typeParam == null) {
                            throw new TestScriptException("Each io object must have a class and type");
                        }
                        NodeList list = io.getChildNodes();
                        if (list.getLength() > 1 || list.item(0) == null || list.item(0).getNodeType() != 3) {
                            throw new TestScriptException("Each IO object must have a value");
                        }
                        dataString = ((Text)list.item(0)).getData();
                        if (typeParam.equals("int")) {
                            try {
                                data = new Test.INT(Integer.parseInt(dataString));
                            }
                            catch (NumberFormatException e1) {
                                throw new TestScriptException("Error parsing integer data");
                            }
                        }
                        if (typeParam.equals("ma")) {
                            try {
                                data = new Test.MA(Integer.parseInt(dataString));
                            }
                            catch (NumberFormatException e1) {
                                throw new TestScriptException("Error parsing integer data");
                            }
                        }
                        if (typeParam.equals("pa")) {
                            try {
                                data = new Test.PA(Integer.parseInt(dataString));
                            }
                            catch (NumberFormatException e1) {
                                throw new TestScriptException("Error parsing integer data");
                            }
                        }
                        if (typeParam.equals("float")) {
                            try {
                                data = new Test.FLOAT(Float.parseFloat(dataString));
                            }
                            catch (NumberFormatException e1) {
                                throw new TestScriptException("Error parsing float data");
                            }
                        }
                        if (!typeParam.equals("char")) break block36;
                        if (dataString == null || dataString.length() != 1 && dataString.length() != 2) {
                            throw new TestScriptException("One-letter String required for Characters");
                        }
                        if (dataString.length() == 2 && dataString.charAt(0) != '\\') {
                            throw new TestScriptException("Two character Character strings must be escape sequences");
                        }
                        if (dataString.length() != 2) break block37;
                        switch (dataString.charAt(1)) {
                            case 'n': {
                                data = new Test.CH('\n');
                                break block38;
                            }
                            case 't': {
                                data = new Test.CH('\t');
                                break block38;
                            }
                            case 'r': {
                                data = new Test.CH('\r');
                                break block38;
                            }
                            case '\\': {
                                data = new Test.CH('\\');
                                break block38;
                            }
                            default: {
                                throw new TestScriptException("Invalid Escape Expression");
                            }
                        }
                    }
                    data = new Test.CH(dataString.charAt(0));
                    break block38;
                }
                if (typeParam.equals("string")) {
                    data = dataString;
                } else {
                    throw new TestScriptException("IO objects must be of type int, char, or float");
                }
            }
            if (classParam.equals("return") && t.getReturnValue() != null) {
                throw new TestScriptException("Only one return value per test allowed");
            }
            if (classParam.equals("return") && typeParam.equals("string")) {
                throw new TestScriptException("String return values are not allowed");
            }
            if (classParam.equals("return")) {
                t.setReturnValue(data);
            } else if (classParam.equals("read")) {
                t.addToRead(data);
            } else if (classParam.equals("write")) {
                t.addToWrite(data);
            } else {
                throw new TestScriptException("IO objects must be of class return, read, or write");
            }
            ++i;
        }
        if (t.getReturnValue() == null) {
            throw new TestScriptException("All test scripts must have a return value specified");
        }
        return t;
    }

    public void save(File toSave) throws TestScriptException {
        Document xmlDoc;
        try {
            xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch (ParserConfigurationException e) {
            throw new TestScriptException("Error with Java XML");
        }
        Element rootElem = xmlDoc.createElement("testscript");
        rootElem.setAttribute("version", "1.0");
        xmlDoc.appendChild(rootElem);
        this.sourceFile = toSave;
        int i = 0;
        while (i < this.tests.size()) {
            Test t = this.tests.get(i);
            Element testElem = xmlDoc.createElement("test");
            testElem.setAttribute("filename", t.getFileName());
            TestScript.addIOType(xmlDoc, testElem, "read", t.getRead());
            TestScript.addIOType(xmlDoc, testElem, "write", t.getWrite());
            TestScript.addIO(xmlDoc, testElem, "return", t.getReturnValue());
            rootElem.appendChild(testElem);
            ++i;
        }
        try {
            XMLUtils.writeXML(xmlDoc, new PrintWriter(new BufferedWriter(new FileWriter(toSave))));
        }
        catch (IOException e) {
            throw new TestScriptException("Error writing XML file");
        }
    }

    protected static void addIOType(Document xmlDoc, Element testElem, String classParam, Collection<?> coll) {
        for (Object o : coll) {
            TestScript.addIO(xmlDoc, testElem, classParam, o);
        }
    }

    protected static void addIO(Document xmlDoc, Element testElem, String classParam, Object o) {
        Element ioElem = xmlDoc.createElement("io");
        ioElem.setAttribute("class", classParam);
        if (o instanceof Test.INT) {
            ioElem.setAttribute("type", "int");
            ioElem.appendChild(xmlDoc.createTextNode(Integer.toString(((Test.INT)o).intValue())));
        } else if (o instanceof Test.FLOAT) {
            ioElem.setAttribute("type", "float");
            ioElem.appendChild(xmlDoc.createTextNode(Float.toString(((Test.FLOAT)o).floatValue())));
        } else if (o instanceof Test.CH) {
            ioElem.setAttribute("type", "char");
            ioElem.appendChild(xmlDoc.createTextNode(Character.toString(((Test.CH)o).charValue())));
        } else if (o instanceof Test.MA) {
            ioElem.setAttribute("type", "ma");
            ioElem.appendChild(xmlDoc.createTextNode(Integer.toString(((Test.MA)o).intValue())));
        } else if (o instanceof Test.PA) {
            ioElem.setAttribute("type", "pa");
            ioElem.appendChild(xmlDoc.createTextNode(Integer.toString(((Test.PA)o).intValue())));
        } else if (o instanceof String) {
            ioElem.setAttribute("type", "string");
            ioElem.appendChild(xmlDoc.createTextNode(o.toString()));
        } else {
            return;
        }
        testElem.appendChild(ioElem);
    }

    public List<Test> getTests() {
        return this.tests;
    }

    public void clearTests() {
        int i = 0;
        while (i < this.tests.size()) {
            this.tests.get(i).clear();
            ++i;
        }
    }

    public void deleteTests() {
        int i = 0;
        while (i < this.tests.size()) {
            if (this.tests.get((int)i).delete) {
                this.tests.remove(i);
                --i;
            }
            ++i;
        }
    }

    public static class Test
    implements Video {
        protected String fileName;
        protected Queue<Object> rqueue = new LinkedList<Object>();
        protected Queue<Object> wqueue = new LinkedList<Object>();
        protected Object returnValue;
        protected boolean completed = false;
        protected Object actualReturnValue;
        protected boolean ioSuccessful = true;
        protected boolean stackCleared = true;
        protected boolean delete;
        protected List<ProgramState> stateSteps = new ArrayList<ProgramState>();
        protected TestScript scriptFile;
        protected Program code = null;

        public Test(String fileName) {
            this.fileName = fileName;
        }

        public void clear() {
            this.completed = false;
            this.ioSuccessful = true;
            this.actualReturnValue = null;
        }

        public void addToRead(Object o) {
            this.rqueue.offer(o);
        }

        public void addToWrite(Object o) {
            this.wqueue.offer(o);
        }

        public Queue<Object> getRead() {
            return this.rqueue;
        }

        public Queue<Object> getWrite() {
            return this.wqueue;
        }

        public String getFileName() {
            if (this.scriptFile != null && this.scriptFile.getSourceFile() != null) {
                File f = new File(this.fileName);
                f = new File(this.scriptFile.getSourceFile().getParent(), f.getName());
                if (f.exists()) {
                    return f.getName();
                }
            }
            return this.fileName;
        }

        public File getFile() {
            File f = new File(this.fileName);
            if (f.exists()) {
                return f;
            }
            if (this.scriptFile != null && this.scriptFile.getSourceFile() != null) {
                f = new File(this.scriptFile.getSourceFile().getParent(), f.getName());
            }
            if (f.exists()) {
                return f;
            }
            return new File(this.fileName);
        }

        public Object getReturnValue() {
            return this.returnValue;
        }

        public void setFileName(String string) {
            this.fileName = string;
        }

        public void setReturnValue(Object o) {
            this.returnValue = o;
        }

        public Object getActualReturnValue() {
            return this.actualReturnValue;
        }

        public boolean isCompleted() {
            return this.completed;
        }

        public boolean isIoSuccessful() {
            return this.ioSuccessful;
        }

        public void delete() {
            this.delete = true;
        }

        public boolean error() {
            if (!this.returnValue.equals(this.actualReturnValue)) {
                return true;
            }
            if (!this.ioSuccessful) {
                return true;
            }
            return !this.stackCleared;
        }

        public boolean isStackCleared() {
            return this.stackCleared;
        }

        public List<ProgramState> getStateSteps() {
            return this.stateSteps;
        }

        public void addStep(ProgramState step) {
            this.stateSteps.add(step);
        }

        public void resetState() {
            this.stateSteps = new ArrayList<ProgramState>();
        }

        public TestScript getScriptFile() {
            return this.scriptFile;
        }

        public void setScriptFile(TestScript file) {
            this.scriptFile = file;
        }

        public void setRead(Collection<?> collection) {
            this.rqueue = new LinkedList(collection);
        }

        public void setWrite(Collection<?> collection) {
            this.wqueue = new LinkedList(collection);
        }

        public void assemble() throws TestScriptException {
            try {
                this.code = SamAssembler.assemble(new BufferedReader(new FileReader(this.getFile())));
            }
            catch (FileNotFoundException e) {
                throw new TestScriptException("Could not find test (" + this.getFileName() + ")");
            }
            catch (AssemblerException e) {
                throw new TestScriptException("Assembler reported error with test " + this.getFileName());
            }
            catch (IOException e) {
                throw new TestScriptException("I/O Error while reading test");
            }
        }

        public Program getCode() throws TestScriptException {
            if (this.code == null) {
                this.assemble();
            }
            return this.code;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public int run(Sys sys, SamThread thread) throws TestScriptException {
            Processor proc = sys.cpu();
            Memory mem = sys.mem();
            proc.init();
            mem.init();
            sys.setVideo(this);
            try {
                proc.load(this.getCode());
                while (proc.get(3) != 1) {
                    if (thread != null && thread.interruptRequested()) {
                        return 0;
                    }
                    int executing = proc.get(0);
                    proc.step();
                    this.addStep(new ProgramState(executing, mem.getStack(), proc.getRegisters()));
                }
                switch (mem.getType(0)) {
                    case CH: {
                        this.actualReturnValue = new CH((char)mem.getValue(0));
                        break;
                    }
                    case FLOAT: {
                        this.actualReturnValue = new FLOAT(Float.intBitsToFloat(mem.getValue(0)));
                        break;
                    }
                    case INT: {
                        this.actualReturnValue = new INT(mem.getValue(0));
                        break;
                    }
                    case PA: {
                        this.actualReturnValue = new PA(mem.getValue(0));
                        break;
                    }
                    case MA: {
                        this.actualReturnValue = new MA(mem.getValue(0));
                        break;
                    }
                    default: {
                        this.actualReturnValue = new String("Error");
                    }
                }
                if (proc.get(1) != 1) {
                    this.stackCleared = false;
                }
            }
            catch (SystemException e) {
                this.actualReturnValue = new String("Error");
            }
            this.completed = true;
            return 4;
        }

        @Override
        public int readInt() {
            if (this.rqueue.isEmpty() || !(this.rqueue.peek() instanceof INT)) {
                this.ioSuccessful = false;
                return 0;
            }
            return ((INT)this.rqueue.remove()).intValue();
        }

        @Override
        public String readString() {
            if (this.rqueue.isEmpty() || !(this.rqueue.peek() instanceof String)) {
                this.ioSuccessful = false;
                return "";
            }
            return (String)this.rqueue.remove();
        }

        @Override
        public char readChar() {
            if (this.rqueue.isEmpty() || !(this.rqueue.peek() instanceof CH)) {
                this.ioSuccessful = false;
                return '\u0000';
            }
            return ((CH)this.rqueue.remove()).charValue();
        }

        @Override
        public float readFloat() {
            if (this.rqueue.isEmpty() || !(this.rqueue.peek() instanceof FLOAT)) {
                this.ioSuccessful = false;
                return 0.0f;
            }
            return ((FLOAT)this.rqueue.remove()).floatValue();
        }

        @Override
        public void writeInt(int a) {
            if (this.wqueue.isEmpty() || !(this.wqueue.peek() instanceof INT) || ((INT)this.wqueue.remove()).intValue() != a) {
                this.ioSuccessful = false;
            }
        }

        @Override
        public void writeFloat(float a) {
            if (this.wqueue.isEmpty() || !(this.wqueue.peek() instanceof FLOAT) || ((FLOAT)this.wqueue.remove()).floatValue() != a) {
                this.ioSuccessful = false;
            }
        }

        @Override
        public void writeChar(char a) {
            if (this.wqueue.isEmpty() || !(this.wqueue.peek() instanceof CH) || ((CH)this.wqueue.remove()).charValue() != a) {
                this.ioSuccessful = false;
            }
        }

        @Override
        public void writeString(String a) {
            if (this.wqueue.isEmpty() || !(this.wqueue.peek() instanceof String) || !a.equals((String)this.wqueue.remove())) {
                this.ioSuccessful = false;
            }
        }

        public static class CH {
            char value;

            public CH(char v) {
                this.value = v;
            }

            public boolean equals(Object o) {
                if (o instanceof CH) {
                    return this.value == ((CH)o).value;
                }
                return false;
            }

            public String toString() {
                return "CH(" + this.value + ")";
            }

            public char charValue() {
                return this.value;
            }
        }

        public static class FLOAT {
            float value;

            public FLOAT(float v) {
                this.value = v;
            }

            public boolean equals(Object o) {
                if (o instanceof FLOAT) {
                    return this.value == ((FLOAT)o).value;
                }
                return false;
            }

            public String toString() {
                return "FLOAT(" + this.value + ")";
            }

            public float floatValue() {
                return this.value;
            }
        }

        public static class INT {
            int value;

            public INT(int v) {
                this.value = v;
            }

            public boolean equals(Object o) {
                if (o instanceof INT) {
                    return this.value == ((INT)o).value;
                }
                return false;
            }

            public String toString() {
                return "INT(" + this.value + ")";
            }

            public int intValue() {
                return this.value;
            }
        }

        public static class MA {
            int value;

            public MA(int v) {
                this.value = v;
            }

            public boolean equals(Object o) {
                if (o instanceof MA) {
                    return this.value == ((MA)o).value;
                }
                return false;
            }

            public String toString() {
                return "MA(" + this.value + ")";
            }

            public int intValue() {
                return this.value;
            }
        }

        public static class PA {
            int value;

            public PA(int v) {
                this.value = v;
            }

            public boolean equals(Object o) {
                if (o instanceof PA) {
                    return this.value == ((PA)o).value;
                }
                return false;
            }

            public String toString() {
                return "PA(" + this.value + ")";
            }

            public int intValue() {
                return this.value;
            }
        }
    }

    public static class TestScriptException
    extends Exception {
        private String message;
        private Throwable t;

        public TestScriptException(String message) {
            this.message = message;
        }

        public TestScriptException(String message, Throwable t) {
            this.message = message;
            this.t = t;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        @Override
        public Throwable getCause() {
            return this.t;
        }
    }

    public static class TestThread
    extends SamThread {
        protected Processor proc;
        protected Sys sys;
        protected List<? extends Test> tests;
        public static final int THREAD_TEST_COMPLETED = 4;

        public TestThread(SamThread.ThreadParent parent, Sys sys, List<? extends Test> tests) {
            this.setParent(parent);
            this.sys = sys;
            this.tests = tests;
            this.proc = sys.cpu();
        }

        public void setTests(List<? extends Test> tests) {
            this.tests = tests;
        }

        public List<? extends Test> getTests() {
            return this.tests;
        }

        public Sys getSys() {
            return this.sys;
        }

        @Override
        public void execute() throws TestScriptException {
            SamThread.ThreadParent parent = this.getParent();
            int i = 0;
            while (i < this.tests.size()) {
                if (this.interruptRequested()) {
                    parent.threadEvent(0, null);
                    return;
                }
                Test test = this.tests.get(i);
                int status = test.run(this.sys, this);
                parent.threadEvent(status, null);
                ++i;
            }
            parent.threadEvent(2, null);
        }
    }
}

