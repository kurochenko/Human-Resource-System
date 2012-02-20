package cz.muni.fi.pa165.hrs.server.jcr;

import org.apache.tika.parser.Parser;
import org.apache.tika.parser.odf.OpenDocumentParser;
import org.apache.tika.parser.pdf.PDFParser;

import static cz.muni.fi.pa165.hrs.server.jcr.JcrType.NODE_CV_ODT;
import static cz.muni.fi.pa165.hrs.server.jcr.JcrType.NODE_CV_PDF;

/**
 * Allowed CV file types which user can store into repository
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
public enum AllowedFileType {

    /** PDF file */
    PDF("application/pdf", NODE_CV_PDF, new PDFParser()),

    /** ODT file */
    ODT("application/vnd.oasis.opendocument.text", NODE_CV_ODT, new OpenDocumentParser());


    /** Mime type of file */
    private String mimeType;

    /** Node type which represents file */
    private JcrType nodeType;

    /** Text extracting parser for extracting text content from file */
    private Parser parser;

    /**
     * Constructor, sets file type attributes
     * @param mimeType mime type of file
     * @param nodeType node type which represents file
     * @param parser Apache Tika parser for given file type
     */
    AllowedFileType(String mimeType, JcrType nodeType, Parser parser) {
        if (mimeType == null) {
            throw new IllegalArgumentException("Mime type is null");
        }
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null");
        }
        if (parser == null) {
            throw new IllegalArgumentException("Parser type is null");
        }
        this.mimeType = mimeType;
        this.nodeType = nodeType;
        this.parser = parser;
    }

    /**
     * Returns mime type of file
     * @return mime type of file
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Returns node type of file
     * @return node type
     */
    public JcrType getNodeType() {
        return nodeType;
    }

    /**
     * Returns Apache Tika parser for given file type
     * @return Apache Tika parser
     */
    public Parser getParser() {
        return parser;
    }
}
