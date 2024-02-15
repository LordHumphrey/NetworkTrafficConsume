package utils;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * @author tao wong
 * @param <T>
 */
public class CountingHttpResponse<T> implements HttpResponse<T> {
    private final HttpResponse<?> original;
    private final T body;

    public CountingHttpResponse(HttpResponse<?> original, T body) {
        this.original = original;
        this.body = body;
    }

    @Override
    public int statusCode() {
        return original.statusCode();
    }

    @Override
    public HttpRequest request() {
        return original.request();
    }

    @Override
    public Optional<HttpResponse<T>> previousResponse() {
        return original.previousResponse().map(response -> new CountingHttpResponse<>(response, body));
    }

    @Override
    public HttpHeaders headers() {
        return original.headers();
    }

    @Override
    public T body() {
        return body;
    }

    @Override
    public Optional<SSLSession> sslSession() {
        return original.sslSession();
    }

    @Override
    public URI uri() {
        return original.uri();
    }

    @Override
    public HttpClient.Version version() {
        return original.version();
    }
}