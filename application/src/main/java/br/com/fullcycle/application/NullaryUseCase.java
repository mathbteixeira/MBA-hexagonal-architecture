package br.com.fullcycle.application;

public abstract class NullaryUseCase<OUTPUT> {

    //1. Cada caso de uso tem um input e um output próprio. Não retorna a entidade, o agregado, ou objeto de valor.
    //2. O caso de uso implementa o padrão Command.

    public abstract OUTPUT execute();

    public <T> T execute(Presenter<OUTPUT, T> presenter) {;
        try {
            return presenter.present(execute());
        } catch (Throwable throwable) {
            return presenter.present(throwable);
        }
    }
}
