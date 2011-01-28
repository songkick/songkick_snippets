package com.songkick.snippets.util;

/**
 * The text body of an {@code EmailMessage}.
 *
 * @author Tom van Zummeren
 * @see EmailMessage
 */
public class Body {
    private String contentType;
    private String content;

    /**
     * Constructs a new {@code Body}.
     *
     * @param contentType content type of the body (usually either "text/plain" or "text/html")
     * @param content     text content of the body
     */
    public Body(String contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    /**
     * Gets the content type of the body (usually either "text/plain" or "text/html").
     *
     * @return content type of the body
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Gets the text content of the body.
     *
     * @return text content of the body
     */
    public String getContent() {
        return content;
    }
}