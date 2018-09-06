package com.curso.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaro;
    private Texture gameOver;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;

    //atributos de configuração
    private int movimento = 0;
    private float larguraDoDispositivo;
    private float alturaDoDispositivo;
    private int estadoJogo = 0;  // 0 Jogo nao iniciado| 1 Jogo iniciado
    private int pontuacao = 0;
    private boolean marcouPonto =  false;


    //configuração da câmera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private Random numeroRandomico;
    private float alturaEntreCanosRandomica;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle rettanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
    //private ShapeRenderer shape;




	@Override
	public void create () {

        batch =  new SpriteBatch();
        passaro = new Texture[3];
        numeroRandomico =  new Random();
        passaroCirculo =  new Circle();
       /* retanguloCanoBaixo =  new Rectangle();
        rettanguloCanoTopo = new Rectangle();
        shape=  new ShapeRenderer();*/
        fonte =  new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        mensagem =  new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);


        passaro[0] = new Texture("passaro1.png");
        passaro[1] = new Texture("passaro2.png");
        passaro[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");
        canoBaixo =  new Texture("cano_baixo.png");
        canoTopo =  new Texture("cano_topo.png");
        gameOver =  new Texture("game_over.png");


        //Câmera
        camera =  new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);
        viewport =  new StretchViewport(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,camera);

        larguraDoDispositivo = VIRTUAL_WIDTH;
        alturaDoDispositivo = VIRTUAL_HEIGHT;


        posicaoInicialVertical = alturaDoDispositivo/2;

        espacoEntreCanos = 400;
        posicaoMovimentoHorizontal = larguraDoDispositivo;

	}

	@Override
	public void render () {

	    camera.update();
	    //limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );


        deltaTime = Gdx.graphics.getDeltaTime();

        variacao += deltaTime * 10;

        if (variacao > 2) variacao = 0;

	    if (estadoJogo == 0){
	        if (Gdx.input.justTouched()){
	                estadoJogo = 1;
            }
        }else {
            velocidadeQueda++;

            if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

	        if (estadoJogo == 1){
                posicaoMovimentoHorizontal -= deltaTime * 200;


                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -15;
                }

                if (posicaoMovimentoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoHorizontal = larguraDoDispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    marcouPonto =  false;
                }
                if (posicaoMovimentoHorizontal<120){
                    if (!marcouPonto) {
                        marcouPonto =  true;
                        pontuacao++;
                    }
                }
            }else {
                if (Gdx.input.justTouched()) {
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDoDispositivo / 2;
                    posicaoMovimentoHorizontal = larguraDoDispositivo;
                }
            }

        }

        batch.setProjectionMatrix(camera.combined);

	    batch.begin();

        batch.draw(fundo ,0 ,0, larguraDoDispositivo,alturaDoDispositivo );

        batch.draw(canoTopo,posicaoMovimentoHorizontal,alturaDoDispositivo/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica );

        batch.draw(canoBaixo,posicaoMovimentoHorizontal,alturaDoDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + alturaEntreCanosRandomica);

        batch.draw(passaro[(int)variacao],120 ,posicaoInicialVertical+velocidadeQueda );

        fonte.draw(batch,String.valueOf(pontuacao),larguraDoDispositivo/2,alturaDoDispositivo-50);

        if (estadoJogo == 2){
            batch.draw(gameOver,larguraDoDispositivo/2-gameOver.getWidth()/2,alturaDoDispositivo/2);
            mensagem.draw(batch,"Toque para Reiniciar",larguraDoDispositivo/2-200,alturaDoDispositivo / 2-gameOver.getHeight()/2);
        }

        batch.end();


        passaroCirculo.set(120 + passaro[0].getWidth()/2,posicaoInicialVertical+passaro[0].getHeight()/2,passaro[0].getWidth()/2);
        retanguloCanoBaixo =  new Rectangle(
                posicaoMovimentoHorizontal,
                alturaDoDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + alturaEntreCanosRandomica,
                canoBaixo.getWidth(),
                canoBaixo.getHeight()
        );

        rettanguloCanoTopo =  new Rectangle(
                posicaoMovimentoHorizontal,
                alturaDoDispositivo/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica,
                canoTopo.getWidth(),
                canoTopo.getHeight()
        );

        // desenhar formas
       /* shape.begin( ShapeRenderer.ShapeType.Filled );
        shape.circle( passaroCirculo.x,passaroCirculo.y,passaroCirculo.radius );
        shape.setColor(Color.RED);
        shape.rect(retanguloCanoBaixo.x,retanguloCanoBaixo.y,retanguloCanoBaixo.width,retanguloCanoBaixo.height);
        shape.rect(rettanguloCanoTopo.x,rettanguloCanoTopo.y,rettanguloCanoTopo.width,rettanguloCanoTopo.height);

        shape.end();*/

        // teste de colisão
        if (Intersector.overlaps(passaroCirculo,retanguloCanoBaixo)||Intersector.overlaps(passaroCirculo,rettanguloCanoTopo)
                || posicaoInicialVertical<=0||posicaoInicialVertical>= alturaDoDispositivo){
            estadoJogo = 2;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }
}
