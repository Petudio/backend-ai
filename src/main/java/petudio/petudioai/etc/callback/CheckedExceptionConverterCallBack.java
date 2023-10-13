package petudio.petudioai.etc.callback;

public interface CheckedExceptionConverterCallBack<T> {

    T call() throws Exception;
}
