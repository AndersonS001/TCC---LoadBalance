# TCC---LoadBalance

Esse projeto foi desenvolvido para atender o Trabalho de Conclusão de Curso dos alunos do 8º semestre/ 2020.01 do curso de Ciência da Computação do Centro Universitário da FEI e a estrutura da aplicação e baseada no Maven utilizando o JDK8.

Essa aplicação tem como objetivo analisar três algoritmos Bioinspirados (Algoritmo Genético, Colônia de Formigas e Algoritmo dos Vagalumes) no processo de alocação de máquinas virtuais em um ambiente cloud utilizando [CloudSim Plus](https://github.com/manoelcampos/cloudsim-plus).


## Configurando o Ambiente

Para executar a aplicação, é necessário possuir instalado e configurado os seguintes programas:

- [Java JDK 8](https://adoptopenjdk.net/?variant=openjdk8&jvmVariant=hotspot)
- [Apache Maven 3.6.3](https://maven.apache.org/download.cgi)
- [IDE Visual Studio Code](https://code.visualstudio.com/Download)
- [Java no Visual Studio Code](https://code.visualstudio.com/docs/languages/java)


## Código Fonte

Para executar a aplicação é necessário clonar o projeto:

git clone https://github.com/AndersonS001/TCC---LoadBalance.git

## Compilação

Para compilar um projeto com o Maven via linha de comando (cmd), executar os comandos abaixo na pasta raiz do projeto:

```shell
mvn clean package
```
O comando irá baixar todas as dependências do Maven e do projeto necessárias para executar a aplicação.
Após compilado com sucesso, entrar na pasta target e executar o comando para iniciar a execução.

```shell
cd target
java -jar AlgoritmosDeBalanceamento.jar
```

Se, utilizar a IDE é necessário apenas pressionar "F5" para iniciar a aplicação

## Resultado
Durante a execução, será gerado o arquivo dados.txt na pasta raiz de início da aplicação com os valores obtidos durante todo processo.