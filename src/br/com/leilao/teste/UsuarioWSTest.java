package br.com.leilao.teste;

import br.com.leilao.modelo.Usuario;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jayway.restassured.RestAssured.*;

public class UsuarioWSTest {

    private Usuario esperado1;
    private Usuario esperado2;

    @Before
    public void setUp(){
        esperado1 = new Usuario(1L, "Mauricio Aniche", "mauricio.aniche@caelum.com.br");
        esperado2 = new Usuario(2L, "Guilherme Silveira", "guilherme.silveira@caelum.com.br");
        //Para configurar a URL base que será consumida pelo Rest-Assured
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    public void deveRetornarListaDeUsuarios(){
        XmlPath path =
                given().
                header("Accept", "application/xml").
                get("/usuarios").andReturn().xmlPath();

        //Para o metodo getObject basta passarmos como parametro:
        //O caminho dentro no XML que contém o objeto e o .class do objeto que será desserializado.
        //Pegando individualmente item por item
        //Usuario usuario1 = path.getObject("list.usuario[0]", Usuario.class);
        //Usuario usuario2 = path.getObject("list.usuario[1]", Usuario.class);
        //Pegando como uma lista de usuarios
        List<Usuario> usuarios = path.getList("list.usuario", Usuario.class);

        Assert.assertEquals(esperado1, usuarios.get(0));
        Assert.assertEquals(esperado2, usuarios.get(1));
    }

    @Test
    public void deveRetornarUsuarioPeloId(){
        JsonPath path = given()
                .header("Accept", "application/json")
                /**O método queryParam() vai passar o parâmetro sempre pela querystring. Já o método parameter() vai observar qual o método foi invocado.
                 * Se o método for "GET", ele passará por querystring. Se o método for "POST", ele passará por baixo dos panos*/
                .parameter("usuario.id", 1)
                .get("/usuarios/show")
                .andReturn().jsonPath();

        Usuario usuario = path.getObject("usuario", Usuario.class);

        Assert.assertEquals(esperado1, usuario);
    }

    @Test
    public void deveAdicionarUmUsuario(){
        Usuario joao = new Usuario("Joao da Silva", "joao@dasilva.com");

        XmlPath path = given()
                    .header("Accept", "application/xml")
                    //O método contentType diz o tipo de dados que vai minha informação
                    .contentType("application/xml")
                    //O metodo body() sabe que o objeto joao deve ser
                    //serializado em xml, pois defini isso no contentType()
                    .body(joao)
                .expect()
                    .statusCode(200)
                .when()
                    .post("/usuarios")
                .andReturn()
                    .xmlPath();

        Usuario resposta = path.getObject("usuario", Usuario.class);

        Assert.assertEquals("Joao da Silva", resposta.getNome());
        Assert.assertEquals("joao@dasilva.com", resposta.getEmail());
    }

    @Test
    public void deveInsereirEDeletarUmUsuario() {
        Usuario joao = new Usuario("Joao da Silva", "joao@dasilva.com");
        XmlPath retorno = given()
                .header("Accept", "application/xml")
                .contentType("application/xml")
                .body(joao)
                .expect()
                .statusCode(200)
                .when()
                .post("/usuarios")
                .andReturn().xmlPath();

        Usuario resposta = retorno.getObject("usuario", Usuario.class);
        Assert.assertEquals("Joao da Silva", resposta.getNome());
        Assert.assertEquals("joao@dasilva.com", resposta.getEmail());
        // deletando aqui
        given()
                .contentType("application/xml").body(resposta)
                .expect().statusCode(200)
                .when().delete("/usuarios/deleta").andReturn().asString();
    }
}
