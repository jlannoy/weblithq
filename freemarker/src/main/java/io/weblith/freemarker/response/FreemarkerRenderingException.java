package io.weblith.freemarker.response;

public class FreemarkerRenderingException extends RuntimeException {

    private static final long serialVersionUID = -583028846189879984L;

    public FreemarkerRenderingException() {
        super();
    }

    public FreemarkerRenderingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FreemarkerRenderingException(String message) {
        super(message);
    }

    public FreemarkerRenderingException(Throwable cause) {
        super(cause);
    }

}
