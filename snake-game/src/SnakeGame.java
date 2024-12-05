import javax.swing.*;  // Importa as classes necessárias para criar a interface gráfica do jogo
import java.awt.*;  // Importa as classes necessárias para manipular gráficos e cores
import java.awt.event.*;  // Importa as classes necessárias para lidar com eventos de teclado e tempo
import java.util.LinkedList;  // Importa a classe LinkedList para armazenar os pontos da cobra
import java.util.Random;  // Importa a classe Random para gerar posições aleatórias para a comida

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    // A classe SnakeGame herda de JPanel para desenhar o jogo na tela e implementa ActionListener e KeyListener
    
    private static final int TILE_SIZE = 20;  // Tamanho de cada "tile" (bloco) da cobra e da comida
    private static final int WIDTH = 400;  // Largura do painel do jogo
    private static final int HEIGHT = 400;  // Altura do painel do jogo
    private static final int INIT_LENGTH = 5;  // Tamanho inicial da cobra
    
    private LinkedList<Point> snake;  // Lista que guarda os pontos que formam a cobra
    private Point food;  // Ponto que representa a comida
    private boolean gameOver;  // Flag que indica se o jogo terminou
    private char direction;  // Direção atual da cobra (U = cima, D = baixo, L = esquerda, R = direita)
    private Timer timer;  // Timer que controla a velocidade do movimento da cobra

    public SnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));  // Define o tamanho do painel do jogo
        this.setBackground(Color.BLACK);  // Define o fundo do painel como preto
        this.setFocusable(true);  // Torna o painel focável para poder receber entradas de teclado
        this.addKeyListener(this);  // Adiciona o KeyListener para capturar teclas pressionadas
        this.snake = new LinkedList<>();  // Cria a lista para armazenar os pontos da cobra
        this.direction = 'R';  // A cobra começa indo para a direita
        this.gameOver = false;  // Inicialmente, o jogo não acabou
        this.timer = new Timer(100, this);  // Cria um timer que chama a ação a cada 100 milissegundos
        initGame();  // Inicializa o jogo
    }

    private void initGame() {
        snake.clear();  // Limpa a lista de pontos da cobra (caso haja algum resto de jogo anterior)
        for (int i = INIT_LENGTH - 1; i >= 0; i--) {
            snake.add(new Point(i, 0));  // Adiciona os pontos iniciais da cobra na posição (i, 0) (topo da tela)
        }
        spawnFood();  // Cria a comida em uma posição aleatória
        timer.start();  // Inicia o timer que vai mover a cobra
    }

    private void spawnFood() {
        Random rand = new Random();  // Cria uma instância da classe Random para gerar números aleatórios
        boolean foodPlaced = false;  // Flag que verifica se a comida foi colocada corretamente
    
        // Continua tentando até encontrar uma posição válida para a comida
        while (!foodPlaced) {
            int x = rand.nextInt(WIDTH / TILE_SIZE);  // Gera uma posição aleatória para o eixo X
            int y = rand.nextInt(HEIGHT / TILE_SIZE);  // Gera uma posição aleatória para o eixo Y
            food = new Point(x, y);  // Cria um ponto para a comida
    
            // Verifica se a comida não está na mesma posição da cobra
            boolean isOccupied = false;
            for (Point p : snake) {
                if (p.equals(food)) {  // Se a comida estiver na mesma posição da cobra, tenta novamente
                    isOccupied = true;
                    break;
                }
            }
    
            if (!isOccupied) {  // Se a comida não está na cobra, coloca ela no painel
                foodPlaced = true;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // Chama o método da superclasse para garantir que o painel seja limpo
        if (gameOver) {  // Se o jogo acabou, desenha a mensagem de "Game Over"
            g.setColor(Color.RED);  // Define a cor do texto como vermelho
            g.setFont(new Font("Arial", Font.BOLD, 30));  // Define a fonte do texto
            g.drawString("Game Over", WIDTH / 3, HEIGHT / 2);  // Desenha a mensagem no centro da tela
        } else {
            // Desenha a cobra
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);  // Obtém o ponto correspondente à parte da cobra
    
                // Se for a cabeça da cobra, desenha em verde escuro
                if (i == 0) {
                    g.setColor(Color.GREEN);  // Cor para a cabeça da cobra
                } else {
                    g.setColor(new Color(0, 100, 0));  // Cor para o corpo da cobra
                }
    
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);  // Desenha o bloco da cobra
            }
    
            // Desenha a comida
            g.setColor(new Color(255, 99, 71));  // Cor vermelha para a maçã
            g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);  // Desenha a comida como um círculo
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            return;  // Se o jogo acabou, não faz mais nada
        }
        moveSnake();  // Move a cobra
        checkCollisions();  // Verifica se houve colisões
        repaint();  // Redesenha o painel para mostrar as mudanças
    }

    private void moveSnake() {
        Point head = snake.getFirst();  // Obtém o ponto da cabeça da cobra
        Point newHead = null;

        // Muda a posição da cabeça da cobra dependendo da direção
        switch (direction) {
            case 'U': newHead = new Point(head.x, head.y - 1); break;  // Para cima
            case 'D': newHead = new Point(head.x, head.y + 1); break;  // Para baixo
            case 'L': newHead = new Point(head.x - 1, head.y); break;  // Para a esquerda
            case 'R': newHead = new Point(head.x + 1, head.y); break;  // Para a direita
        }

        snake.addFirst(newHead);  // Adiciona a nova cabeça à frente da cobra

        // Se a cobra comeu a comida, a comida é reposicionada
        if (newHead.equals(food)) {
            spawnFood();  // Coloca a comida em uma nova posição
        } else {
            snake.removeLast();  // Se não comeu, remove a última parte da cobra (a cauda)
        }
    }

    private void checkCollisions() {
        Point head = snake.getFirst();  // Obtém o ponto da cabeça da cobra

        // Verifica se a cabeça da cobra bateu nas bordas da tela
        if (head.x < 0 || head.x >= WIDTH / TILE_SIZE || head.y < 0 || head.y >= HEIGHT / TILE_SIZE) {
            gameOver = true;  // Se bateu, o jogo acabou
        }

        // Verifica se a cabeça bateu no corpo da cobra
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;  // Se a cabeça se sobrepõe a qualquer parte do corpo, o jogo acabou
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;  // Se o jogo acabou, não processa mais teclas

        int keyCode = e.getKeyCode();  // Obtém o código da tecla pressionada
        if (keyCode == KeyEvent.VK_UP && direction != 'D') {
            direction = 'U';  // Se pressionar para cima, muda a direção (exceto se já for para baixo)
        } else if (keyCode == KeyEvent.VK_DOWN && direction != 'U') {
            direction = 'D';  // Se pressionar para baixo, muda a direção (exceto se já for para cima)
        } else if (keyCode == KeyEvent.VK_LEFT && direction != 'R') {
            direction = 'L';  // Se pressionar para esquerda, muda a direção (exceto se já for para a direita)
        } else if (keyCode == KeyEvent.VK_RIGHT && direction != 'L') {
            direction = 'R';  // Se pressionar para direita, muda a direção (exceto se já for para a esquerda)
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}  // Não faz nada quando a tecla é liberada

    @Override
    public void keyTyped(KeyEvent e) {}  // Não faz nada quando a tecla é digitada

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");  // Cria a janela do jogo
        SnakeGame gamePanel = new SnakeGame();  // Cria uma instância do painel do jogo
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Define o comportamento de fechamento da janela
        frame.add(gamePanel);  // Adiciona o painel do jogo à janela
        frame.pack();  // Ajusta o tamanho da janela para o tamanho do painel
        frame.setVisible(true);  // Torna a janela visível
    }
}