package petudio.petudioai.etc.callback;

/**
 * 가급적 checked exception을 발생시키는 로직에 대해서만 사용한다.
 * checked excpetion을 runtime exception으로 바꿔서 던진다 그에따라 method signature에 checked exception을 추가하는 것을 방지 한다.
 */
public class CheckedExceptionConverterTemplate {

    public <T> T execute(CheckedExceptionConverterCallBack<T> checkedExceptionConverterCallBack) {
        try {
            return checkedExceptionConverterCallBack.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
