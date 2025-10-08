package br.com.fullcycle.hexagonal.application;

public abstract class UseCase<INPUT, OUTPUT> {

    //1. Cada caso de uso tem um input e um output próprio. Não retorna a entidade, o agregado, ou objeto de valor.
    //2. O caso de uso implementa o padrão Command.

    public abstract OUTPUT execute(INPUT input);
}
