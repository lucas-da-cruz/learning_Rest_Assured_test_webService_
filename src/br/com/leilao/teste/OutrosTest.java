package br.com.leilao.teste;

import org.junit.Test;
import static com.jayway.restassured.RestAssured.*;

public class OutrosTest {
    /**
     *  Testa se a requisição gera um cookie
     */
    @Test
    public void deveGerarUmCookie(){
        expect()
                .cookie("rest-assured", "funciona")
                .get("/cookie/teste");
    }

    @Test
    public void deveGerarUmHeader(){
        expect()
                .header("novo-header", "abc")
                .get("/cookie/teste");
    }
}
