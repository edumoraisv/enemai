package com.enemai.util;

public class api {

    private String urlGerarAcesso = "https://basenove.com/enemai/api/v1/gerarAcesso.php";
    private String urlCodeAcesso = "https://basenove.com/p/";
    private String urlInfoAcesso = "https://basenove.com/enemai/api/v1/obterInfoPermissao.php";
    private String urlPagamento = "https://basenove.com/enemai/api/v1/pagamento.php";
    private String urlTemas = "https://basenove.com/enemai/api/v1/obterTemas.php";

    public String getUrlGerarAcesso() {
        return urlGerarAcesso;
    }

    public String getUrlCodeAcesso() {
        return urlCodeAcesso;
    }

    public String getUrlInfoPermissao() {
        return urlInfoAcesso;
    }

    public String getUrlPagamento() {
        return urlPagamento;
    }

    public String getUrlTemas() {
        return urlTemas;
    }
}
