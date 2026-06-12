package UserInterface3D;

import model3d.Elemento3D;
import model3d.No3D;
import model3d.Trelica3D;
import model3d.Vinculo3D;
import solver3d.CalculadoraTrelica3D;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PainelTrelica3D extends JPanel {

    private DefaultListModel<String> modeloAlgebra = new DefaultListModel<>();
    private JList<String> listaAlgebra = new JList<>(modeloAlgebra);
    private JTextField campoEntrada = new JTextField();
    private JTextArea areaResultado = new JTextArea();
    private PainelEspaco3D espaco3D = new PainelEspaco3D();
    private PainelPropriedades painelPropriedades = new PainelPropriedades();

    private Map<Integer, NoItem> nos = new LinkedHashMap<>();
    private Map<Integer, BarraItem> barras = new LinkedHashMap<>();
    private Map<Integer, VinculoItem> vinculos = new LinkedHashMap<>();
    private Object selecionado;
    private int proximoNo = 1;
    private int proximaBarra = 1;
    private boolean atualizandoLista = false;

    public PainelTrelica3D() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        configurarAreaResultado();

        add(criarBarraSuperior(), BorderLayout.NORTH);
        add(criarCorpo(), BorderLayout.CENTER);
        carregarExemplo();
    }

    private JComponent criarBarraSuperior() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 218)));
        topo.setBackground(new Color(250, 250, 252));

        JPanel comandos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        comandos.setOpaque(false);

        JButton btnExemplo = criarBotao("Exemplo", "Carregar trelica 3D de exemplo");
        JButton btnLimpar = criarBotao("Limpar", "Limpar todos os elementos");
        JButton btnRemover = criarBotao("Remover selecionado", "Remover o nó, barra ou vínculo selecionado");
        JButton btnCalcular = criarBotao("Calcular", "Resolver a trelica espacial");
        JButton btnVista = criarBotao("Centralizar", "Centralizar a vista 3D");
        JButton btnAjuda = criarBotao("Ajuda", "Mostrar ajuda do modo 3D");
        estilizarBotaoPrincipal(btnCalcular);

        btnExemplo.addActionListener(e -> acionarExemplo(btnExemplo));
        btnLimpar.addActionListener(e -> limparTudo());
        btnRemover.addActionListener(e -> removerSelecionado());
        btnCalcular.addActionListener(e -> calcular());
        btnVista.addActionListener(e -> espaco3D.resetarVista());
        btnAjuda.addActionListener(e -> mostrarAjuda());

        comandos.add(btnExemplo);
        comandos.add(btnLimpar);
        comandos.add(btnRemover);
        comandos.add(btnCalcular);
        comandos.add(btnVista);
        comandos.add(btnAjuda);

        topo.add(comandos, BorderLayout.WEST);
        return topo;
    }

    private JComponent criarCorpo() {
        // Layout principal inspirado no GeoGebra: álgebra à esquerda, espaço 3D no centro
        // e painel de propriedades/resultado à direita.
        JSplitPane esquerdaCentro = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            criarPainelAlgebra(),
            espaco3D);
        esquerdaCentro.setResizeWeight(0.24);
        esquerdaCentro.setContinuousLayout(true);

        JSplitPane direita = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            painelPropriedades,
            new JScrollPane(areaResultado));
        direita.setResizeWeight(0.62);
        direita.setContinuousLayout(true);
        direita.setPreferredSize(new Dimension(310, 0));

        JSplitPane corpo = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            esquerdaCentro,
            direita);
        corpo.setResizeWeight(0.78);
        corpo.setContinuousLayout(true);
        return corpo;
    }

    private void configurarAreaResultado() {
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaResultado.setBackground(new Color(24, 26, 32));
        areaResultado.setForeground(new Color(225, 235, 255));
        areaResultado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private JComponent criarPainelAlgebra() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setPreferredSize(new Dimension(330, 0));
        painel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(210, 210, 218)));
        painel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("  Elementos");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        titulo.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        listaAlgebra.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaAlgebra.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        listaAlgebra.addListSelectionListener(e -> {
            if (!atualizandoLista && !e.getValueIsAdjusting()) selecionarPorIndice(listaAlgebra.getSelectedIndex());
        });

        JPanel entrada = new JPanel(new BorderLayout());
        entrada.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 210, 218)));
        JButton btnAdicionar = criarBotao("+", "Adicionar elemento ou executar entrada");
        JButton btnExcluir = criarBotao("-", "Excluir o item selecionado");
        btnAdicionar.setPreferredSize(new Dimension(44, 36));
        btnExcluir.setPreferredSize(new Dimension(44, 36));
        campoEntrada.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(210, 210, 218)),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        campoEntrada.setToolTipText("Ex.: No(1,2,3), Barra(1,2), Barra(0,0,0, 2,0,1), Vinculo(1,1,1,1)");
        campoEntrada.addActionListener(e -> executarEntrada());
        btnAdicionar.addActionListener(e -> {
            if (campoEntrada.getText().trim().isEmpty()) {
                mostrarMenuAdicionar(btnAdicionar);
            } else {
                executarEntrada();
            }
        });
        btnExcluir.addActionListener(e -> removerSelecionado());

        JPanel botoesEntrada = new JPanel(new GridLayout(1, 2));
        botoesEntrada.add(btnAdicionar);
        botoesEntrada.add(btnExcluir);

        entrada.add(botoesEntrada, BorderLayout.WEST);
        entrada.add(campoEntrada, BorderLayout.CENTER);

        JTextArea ajuda = new JTextArea(
            "Entrada por argumentos:\n" +
            "No(x,y,z)\n" +
            "Barra(noA,noB)\n" +
            "Barra(x1,y1,z1,x2,y2,z2)\n" +
            "Vinculo(no,rx,ry,rz)\n" +
            "Forca(no,fx,fy,fz)");
        ajuda.setEditable(false);
        ajuda.setFocusable(false);
        ajuda.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        ajuda.setForeground(new Color(100, 100, 108));
        ajuda.setBackground(new Color(250, 250, 252));
        ajuda.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(entrada, BorderLayout.CENTER);
        topo.add(ajuda, BorderLayout.SOUTH);

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(listaAlgebra), BorderLayout.CENTER);
        return painel;
    }

    private JButton criarBotao(String texto, String tooltip) {
        JButton botao = new JButton(texto);
        botao.setToolTipText(tooltip);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private void estilizarBotaoPrincipal(JButton botao) {
        botao.setUI(new BasicButtonUI());
        botao.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botao.setBackground(new Color(30, 120, 60));
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(true);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(20, 90, 45)),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void mostrarMenuAdicionar(Component origem) {
        JPopupMenu menu = new JPopupMenu();
        adicionarItemMenu(menu, "Novo nó", "No", "0,0,0", "Coordenadas X,Y,Z");
        adicionarItemMenu(menu, "Nova barra por nós", "Barra", "1,2", "IDs dos nós inicial e final");
        adicionarItemMenu(menu, "Nova barra por coordenadas", "Barra", "0,0,0, 2,0,0", "X1,Y1,Z1,X2,Y2,Z2");
        adicionarItemMenu(menu, "Novo vínculo", "Vinculo", "1,1,1,1", "No, restringeX, restringeY, restringeZ");
        adicionarItemMenu(menu, "Nova força", "Forca", "1,0,0,-100", "No, Fx, Fy, Fz");
        menu.show(origem, 0, origem.getHeight());
    }

    private void adicionarItemMenu(JPopupMenu menu, String texto, String comando, String modelo, String orientacao) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> {
            String resposta = JOptionPane.showInputDialog(this, orientacao, modelo);
            if (resposta == null || resposta.trim().isEmpty()) return;
            try {
                interpretarComando(comando + "(" + resposta.trim() + ")");
                campoEntrada.setText("");
                atualizarTudo();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Entrada invalida", JOptionPane.WARNING_MESSAGE);
            }
        });
        menu.add(item);
    }

    private void mostrarAjuda() {
        String html = "<html><body style='font-family:sans-serif; width:520px; padding:8px'>" +
            "<h2>Ajuda do modo 3D</h2>" +
            "<h3>Entrada de elementos</h3>" +
            "<p>Use a caixa de entrada no painel Elementos. O botão <b>+</b> abre modelos prontos; " +
            "pressione Enter ou clique novamente no <b>+</b> para executar a entrada preenchida.</p>" +
            "<ul>" +
            "<li><b>No(x,y,z)</b>: cria um nó com três coordenadas.</li>" +
            "<li><b>Barra(noA,noB)</b>: cria uma barra entre dois nós existentes.</li>" +
            "<li><b>Barra(x1,y1,z1,x2,y2,z2)</b>: cria os dois nós, se necessário, e conecta a barra.</li>" +
            "<li><b>Vinculo(no,rx,ry,rz)</b>: define restrições no nó. Use 1 para restringir e 0 para liberar.</li>" +
            "<li><b>Forca(no,fx,fy,fz)</b>: aplica força externa ao nó.</li>" +
            "</ul>" +
            "<h3>Interação no plano 3D</h3>" +
            "<ul>" +
            "<li>Arraste com o mouse para girar a visualização.</li>" +
            "<li>Use a roda do mouse para aproximar ou afastar.</li>" +
            "<li>Segure Shift ou use botão direito enquanto arrasta para mover a vista.</li>" +
            "<li>Clique em um nó ou barra para selecionar e editar seus argumentos.</li>" +
            "</ul>" +
            "<h3>Configuração do nó</h3>" +
            "<p>Ao selecionar um nó, o painel Propriedades permite editar coordenadas, forças Fx/Fy/Fz " +
            "e restrições de vínculo em X/Y/Z. Quando existir vínculo, os campos Rx/Ry/Rz mostram as reações " +
            "e podem ser editados manualmente; ao calcular, o solver atualiza esses valores.</p>" +
            "<h3>Configuração do vínculo</h3>" +
            "<p>Ao selecionar um vínculo na lista Elementos, é possível alterar o nó, o tipo de apoio, " +
            "e quais restrições X/Y/Z estão ativas.</p>" +
            "<h3>Remoção</h3>" +
            "<p>Use <b>Remover selecionado</b> para apagar o item ativo. Ao remover um nó, suas barras conectadas " +
            "e seu vínculo também são removidos. O botão <b>-</b> ao lado da entrada executa a mesma remoção.</p>" +
            "<h3>Cálculo</h3>" +
            "<p>O cálculo 3D usa a condição de isostasia <b>m + r = 3n</b>. " +
            "Depois de calcular, barras em azul indicam tração e barras em vermelho indicam compressão.</p>" +
            "</body></html>";

        JEditorPane editor = new JEditorPane("text/html", html);
        editor.setEditable(false);
        editor.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(editor);
        scroll.setPreferredSize(new Dimension(600, 520));
        JOptionPane.showMessageDialog(this, scroll, "Ajuda - Aplicativo 3D", JOptionPane.PLAIN_MESSAGE);
    }

    private void executarEntrada() {
        String texto = campoEntrada.getText().trim();
        if (texto.isEmpty()) return;
        try {
            interpretarComando(texto);
            campoEntrada.setText("");
            atualizarTudo();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Entrada invalida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void interpretarComando(String texto) throws Exception {
        // Entrada textual por elementos e argumentos:
        // No(x,y,z), Barra(noA,noB), Barra(x1,y1,z1,x2,y2,z2), Vinculo(no,rx,ry,rz).
        Matcher m = Pattern.compile("^\\s*([A-Za-zçÇãÃ]+)\\s*\\((.*)\\)\\s*$").matcher(texto);
        if (!m.matches()) throw new Exception("Use o formato Nome(arg1,arg2,...).");

        String nome = normalizar(m.group(1));
        String[] args = separarArgumentos(m.group(2));
        if (nome.equals("no") || nome.equals("n")) {
            exigirQuantidade(args, 3, "No(x,y,z)");
            adicionarNo(numero(args[0]), numero(args[1]), numero(args[2]));
        } else if (nome.equals("barra") || nome.equals("b")) {
            if (args.length == 2) {
                adicionarBarraPorNos(inteiro(args[0]), inteiro(args[1]));
            } else if (args.length == 6) {
                NoItem a = adicionarNo(numero(args[0]), numero(args[1]), numero(args[2]));
                NoItem b = adicionarNo(numero(args[3]), numero(args[4]), numero(args[5]));
                adicionarBarraPorNos(a.id, b.id);
            } else {
                throw new Exception("Use Barra(noA,noB) ou Barra(x1,y1,z1,x2,y2,z2).");
            }
        } else if (nome.equals("vinculo") || nome.equals("v")) {
            exigirQuantidade(args, 4, "Vinculo(no,rx,ry,rz)");
            adicionarOuAtualizarVinculo(inteiro(args[0]), logico(args[1]), logico(args[2]), logico(args[3]));
        } else if (nome.equals("forca") || nome.equals("f")) {
            exigirQuantidade(args, 4, "Forca(no,fx,fy,fz)");
            NoItem no = noObrigatorio(inteiro(args[0]));
            no.fx = numero(args[1]);
            no.fy = numero(args[2]);
            no.fz = numero(args[3]);
        } else {
            throw new Exception("Comando desconhecido: " + m.group(1));
        }
    }

    private String normalizar(String texto) {
        return texto.toLowerCase()
            .replace("ó", "o")
            .replace("ã", "a")
            .replace("ç", "c");
    }

    private String[] separarArgumentos(String texto) {
        if (texto.trim().isEmpty()) return new String[0];
        String[] partes = texto.split(",");
        for (int i = 0; i < partes.length; i++) partes[i] = partes[i].trim();
        return partes;
    }

    private void exigirQuantidade(String[] args, int total, String exemplo) throws Exception {
        if (args.length != total) throw new Exception("Use " + exemplo + ".");
    }

    private double numero(String valor) throws Exception {
        try {
            return Double.parseDouble(valor.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Numero invalido: " + valor);
        }
    }

    private int inteiro(String valor) throws Exception {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Inteiro invalido: " + valor);
        }
    }

    private boolean logico(String valor) throws Exception {
        String v = valor.trim().toLowerCase();
        if (v.equals("1") || v.equals("true") || v.equals("sim") || v.equals("x")) return true;
        if (v.equals("0") || v.equals("false") || v.equals("nao") || v.equals("não") || v.equals("-")) return false;
        throw new Exception("Restricao deve ser 1/0, true/false ou sim/nao.");
    }

    private NoItem adicionarNo(double x, double y, double z) {
        NoItem existente = encontrarNoPorCoordenadas(x, y, z);
        if (existente != null) return existente;
        NoItem no = new NoItem(proximoNo++, x, y, z);
        nos.put(no.id, no);
        selecionado = no;
        return no;
    }

    private NoItem encontrarNoPorCoordenadas(double x, double y, double z) {
        for (NoItem no : nos.values()) {
            if (Math.abs(no.x - x) < 1e-9 && Math.abs(no.y - y) < 1e-9 && Math.abs(no.z - z) < 1e-9) return no;
        }
        return null;
    }

    private void adicionarBarraPorNos(int idA, int idB) throws Exception {
        if (idA == idB) throw new Exception("A barra precisa de dois nos diferentes.");
        noObrigatorio(idA);
        noObrigatorio(idB);
        for (BarraItem barra : barras.values()) {
            if ((barra.noA == idA && barra.noB == idB) || (barra.noA == idB && barra.noB == idA)) {
                selecionado = barra;
                return;
            }
        }
        BarraItem barra = new BarraItem(proximaBarra++, idA, idB);
        barras.put(barra.id, barra);
        selecionado = barra;
    }

    private void adicionarOuAtualizarVinculo(int idNo, boolean rx, boolean ry, boolean rz) throws Exception {
        noObrigatorio(idNo);
        VinculoItem vinculo = vinculos.get(idNo);
        if (vinculo == null) {
            vinculo = new VinculoItem(idNo);
            vinculos.put(idNo, vinculo);
        }
        vinculo.rx = rx;
        vinculo.ry = ry;
        vinculo.rz = rz;
        selecionado = vinculo;
    }

    private NoItem noObrigatorio(int id) throws Exception {
        NoItem no = nos.get(id);
        if (no == null) throw new Exception("No " + id + " nao existe.");
        return no;
    }

    private void selecionarPorIndice(int indice) {
        ArrayList<Object> elementos = elementosOrdenados();
        if (indice >= 0 && indice < elementos.size()) {
            selecionado = elementos.get(indice);
            painelPropriedades.atualizar();
            espaco3D.repaint();
        }
    }

    private ArrayList<Object> elementosOrdenados() {
        ArrayList<Object> elementos = new ArrayList<>();
        elementos.addAll(nos.values());
        elementos.addAll(barras.values());
        elementos.addAll(vinculos.values());
        return elementos;
    }

    private void selecionarObjeto(Object objeto) {
        selecionado = objeto;
        painelPropriedades.atualizar();
        atualizarAlgebra();
        espaco3D.repaint();
    }

    private void atualizarTudo() {
        atualizarAlgebra();
        painelPropriedades.atualizar();
        espaco3D.repaint();
    }

    private void atualizarAlgebra() {
        Object atual = selecionado;
        atualizandoLista = true;
        try {
            modeloAlgebra.clear();
            int indiceSelecionado = -1;
            ArrayList<Object> elementos = elementosOrdenados();
            for (int i = 0; i < elementos.size(); i++) {
                Object obj = elementos.get(i);
                modeloAlgebra.addElement(descrever(obj));
                if (obj == atual) indiceSelecionado = i;
            }
            if (indiceSelecionado >= 0) listaAlgebra.setSelectedIndex(indiceSelecionado);
        } finally {
            atualizandoLista = false;
        }
    }

    private String descrever(Object obj) {
        if (obj instanceof NoItem) {
            NoItem no = (NoItem)obj;
            return String.format("N%d = (%.2f, %.2f, %.2f)", no.id, no.x, no.y, no.z);
        }
        if (obj instanceof BarraItem) {
            BarraItem barra = (BarraItem)obj;
            return String.format("B%d = Barra(N%d, N%d)", barra.id, barra.noA, barra.noB);
        }
        VinculoItem vinculo = (VinculoItem)obj;
        return String.format("V(N%d) = [%s %s %s]", vinculo.noId,
            vinculo.rx ? "Rx" : "--", vinculo.ry ? "Ry" : "--", vinculo.rz ? "Rz" : "--");
    }

    private void calcular() {
        try {
            Trelica3D trelica = montarTrelica();
            CalculadoraTrelica3D calculadora = new CalculadoraTrelica3D(trelica);
            calculadora.resolver();
            copiarResultados(trelica);
            areaResultado.setText(calculadora.getRelatorio());
            atualizarTudo();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no calculo 3D", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Trelica3D montarTrelica() throws Exception {
        Trelica3D trelica = new Trelica3D();
        Map<Integer, No3D> nosSolver = new LinkedHashMap<>();

        for (NoItem n : nos.values()) {
            No3D no = new No3D(n.id, n.x, n.y, n.z, n.fx, n.fy, n.fz);
            nosSolver.put(n.id, no);
            trelica.adicionarNo(no);
        }

        for (BarraItem b : barras.values()) {
            No3D a = nosSolver.get(b.noA);
            No3D c = nosSolver.get(b.noB);
            if (a == null || c == null) throw new Exception("Barra " + b.id + " referencia no inexistente.");
            trelica.adicionarElemento(new Elemento3D(b.id, a, c));
        }

        for (VinculoItem v : vinculos.values()) {
            No3D no = nosSolver.get(v.noId);
            if (no == null) throw new Exception("Vinculo referencia no inexistente: " + v.noId);
            trelica.adicionarVinculo(new Vinculo3D(no, v.rx, v.ry, v.rz));
        }
        return trelica;
    }

    private void copiarResultados(Trelica3D trelica) {
        for (BarraItem barra : barras.values()) barra.forcaInterna = 0;
        for (VinculoItem vinculo : vinculos.values()) {
            vinculo.reacaoX = 0;
            vinculo.reacaoY = 0;
            vinculo.reacaoZ = 0;
        }
        for (Elemento3D e : trelica.getElementos()) {
            BarraItem barra = barras.get(e.getId());
            if (barra != null) barra.forcaInterna = e.getForcaInterna();
        }
        for (Vinculo3D v : trelica.getVinculos()) {
            VinculoItem vinculo = vinculos.get(v.getNo().getId());
            if (vinculo != null) {
                vinculo.reacaoX = v.getReacaoX();
                vinculo.reacaoY = v.getReacaoY();
                vinculo.reacaoZ = v.getReacaoZ();
            }
        }
    }

    private void acionarExemplo(Component origem) {
        if (estaVazio()) {
            carregarExemplo();
            return;
        }

        JPopupMenu menu = new JPopupMenu();
        JMenuItem substituir = new JMenuItem("Substituir tudo pelo exemplo");
        substituir.addActionListener(e -> confirmarCarregarExemplo());
        menu.add(substituir);
        menu.show(origem, 0, origem.getHeight());
    }

    private boolean estaVazio() {
        return nos.isEmpty() && barras.isEmpty() && vinculos.isEmpty();
    }

    private void confirmarCarregarExemplo() {
        int opcao = JOptionPane.showConfirmDialog(
            this,
            "Tudo será excluído para carregar o exemplo 3D. Continuar?",
            "Carregar exemplo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION) {
            carregarExemplo();
        }
    }

    private void carregarExemplo() {
        limparTudo();
        NoItem n1 = adicionarNo(0, 0, 0);
        NoItem n2 = adicionarNo(4, 0, 0);
        NoItem n3 = adicionarNo(0, 4, 0);
        NoItem n4 = adicionarNo(0, 0, 4);
        n4.fz = -100;
        try {
            adicionarBarraPorNos(n1.id, n2.id);
            adicionarBarraPorNos(n1.id, n3.id);
            adicionarBarraPorNos(n1.id, n4.id);
            adicionarBarraPorNos(n2.id, n3.id);
            adicionarBarraPorNos(n2.id, n4.id);
            adicionarBarraPorNos(n3.id, n4.id);
            adicionarOuAtualizarVinculo(n1.id, true, true, true);
            adicionarOuAtualizarVinculo(n2.id, false, true, true);
            adicionarOuAtualizarVinculo(n3.id, false, false, true);
        } catch (Exception ignored) {
        }
        selecionado = n4;
        areaResultado.setText("");
        atualizarTudo();
    }

    private void limparTudo() {
        nos.clear();
        barras.clear();
        vinculos.clear();
        selecionado = null;
        proximoNo = 1;
        proximaBarra = 1;
        areaResultado.setText("");
        atualizarTudo();
    }

    private void removerSelecionado() {
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um nó, barra ou vínculo para remover.", "Remover", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (selecionado instanceof NoItem) {
            NoItem no = (NoItem)selecionado;
            nos.remove(no.id);
            vinculos.remove(no.id);
            barras.values().removeIf(b -> b.noA == no.id || b.noB == no.id);
        } else if (selecionado instanceof BarraItem) {
            BarraItem barra = (BarraItem)selecionado;
            barras.remove(barra.id);
        } else if (selecionado instanceof VinculoItem) {
            VinculoItem vinculo = (VinculoItem)selecionado;
            vinculos.remove(vinculo.noId);
        }

        selecionado = null;
        areaResultado.setText("");
        atualizarTudo();
    }

    private class PainelPropriedades extends JPanel {
        private CardLayout cards = new CardLayout();
        private JPanel conteudo = new JPanel(cards);
        private JTextField campoX = campo();
        private JTextField campoY = campo();
        private JTextField campoZ = campo();
        private JTextField campoFx = campo();
        private JTextField campoFy = campo();
        private JTextField campoFz = campo();
        private JTextField campoReacaoX = campo();
        private JTextField campoReacaoY = campo();
        private JTextField campoReacaoZ = campo();
        private JTextField campoVinculoNo = campo();
        private JTextField campoVinculoReacaoX = campo();
        private JTextField campoVinculoReacaoY = campo();
        private JTextField campoVinculoReacaoZ = campo();
        private JCheckBox chkRx = new JCheckBox("Restringe X");
        private JCheckBox chkRy = new JCheckBox("Restringe Y");
        private JCheckBox chkRz = new JCheckBox("Restringe Z");
        private JCheckBox chkVinculoRx = new JCheckBox("Restringe X");
        private JCheckBox chkVinculoRy = new JCheckBox("Restringe Y");
        private JCheckBox chkVinculoRz = new JCheckBox("Restringe Z");
        private JLabel tituloNo = new JLabel();
        private JLabel tituloVinculo = new JLabel();
        private JLabel infoBarra = new JLabel();
        private boolean atualizandoCampos = false;

        PainelPropriedades() {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(260, 0));
            setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(210, 210, 218)));
            add(criarTitulo(), BorderLayout.NORTH);
            conteudo.add(criarVazio(), "vazio");
            conteudo.add(criarNo(), "no");
            conteudo.add(criarBarra(), "barra");
            conteudo.add(criarVinculo(), "vinculo");
            add(conteudo, BorderLayout.CENTER);
            atualizar();
        }

        private JComponent criarTitulo() {
            JLabel titulo = new JLabel("  Propriedades");
            titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            return titulo;
        }

        private JComponent criarVazio() {
            JLabel msg = new JLabel("<html><center>Selecione um nó,<br>barra ou vínculo</center></html>", SwingConstants.CENTER);
            msg.setForeground(new Color(130, 130, 138));
            return msg;
        }

        private JComponent criarNo() {
            // O nó selecionado concentra coordenadas, força externa e restrições de apoio,
            // no mesmo espírito do painel lateral do modo 2D.
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            tituloNo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            p.add(tituloNo);
            p.add(Box.createVerticalStrut(10));
            p.add(linha("X", campoX));
            p.add(linha("Y", campoY));
            p.add(linha("Z", campoZ));
            p.add(separador());
            p.add(linha("Fx", campoFx));
            p.add(linha("Fy", campoFy));
            p.add(linha("Fz", campoFz));
            p.add(separador());
            p.add(chkRx);
            p.add(chkRy);
            p.add(chkRz);
            p.add(separador());
            p.add(new JLabel("Reações do vínculo"));
            configurarCampoResultado(campoReacaoX);
            configurarCampoResultado(campoReacaoY);
            configurarCampoResultado(campoReacaoZ);
            p.add(linha("Rx", campoReacaoX));
            p.add(linha("Ry", campoReacaoY));
            p.add(linha("Rz", campoReacaoZ));

            DocumentListener listener = new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { aplicarNoSelecionado(); }
                public void removeUpdate(DocumentEvent e) { aplicarNoSelecionado(); }
                public void changedUpdate(DocumentEvent e) { aplicarNoSelecionado(); }
            };
            campoX.getDocument().addDocumentListener(listener);
            campoY.getDocument().addDocumentListener(listener);
            campoZ.getDocument().addDocumentListener(listener);
            campoFx.getDocument().addDocumentListener(listener);
            campoFy.getDocument().addDocumentListener(listener);
            campoFz.getDocument().addDocumentListener(listener);
            chkRx.addActionListener(e -> aplicarNoSelecionado());
            chkRy.addActionListener(e -> aplicarNoSelecionado());
            chkRz.addActionListener(e -> aplicarNoSelecionado());
            return p;
        }

        private JComponent criarBarra() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            infoBarra.setVerticalAlignment(SwingConstants.TOP);
            p.add(infoBarra, BorderLayout.NORTH);
            return p;
        }

        private JComponent criarVinculo() {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

            tituloVinculo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

            p.add(tituloVinculo);
            p.add(Box.createVerticalStrut(10));
            p.add(linha("Nó", campoVinculoNo));
            p.add(Box.createVerticalStrut(8));
            p.add(chkVinculoRx);
            p.add(chkVinculoRy);
            p.add(chkVinculoRz);
            p.add(separador());
            p.add(new JLabel("Reações calculadas"));
            configurarCampoResultado(campoVinculoReacaoX);
            configurarCampoResultado(campoVinculoReacaoY);
            configurarCampoResultado(campoVinculoReacaoZ);
            p.add(linha("Rx", campoVinculoReacaoX));
            p.add(linha("Ry", campoVinculoReacaoY));
            p.add(linha("Rz", campoVinculoReacaoZ));

            DocumentListener listener = new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { aplicarVinculoSelecionado(); }
                public void removeUpdate(DocumentEvent e) { aplicarVinculoSelecionado(); }
                public void changedUpdate(DocumentEvent e) { aplicarVinculoSelecionado(); }
            };
            campoVinculoNo.getDocument().addDocumentListener(listener);
            chkVinculoRx.addActionListener(e -> aplicarVinculoSelecionado());
            chkVinculoRy.addActionListener(e -> aplicarVinculoSelecionado());
            chkVinculoRz.addActionListener(e -> aplicarVinculoSelecionado());
            return p;
        }

        private JTextField campo() {
            JTextField c = new JTextField();
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            return c;
        }

        private void configurarCampoResultado(JTextField campo) {
            campo.setEditable(false);
            campo.setFocusable(false);
            campo.setBackground(new Color(240, 242, 246));
            campo.setForeground(new Color(70, 74, 84));
        }

        private JComponent linha(String rotulo, JTextField campo) {
            JPanel linha = new JPanel(new BorderLayout(8, 0));
            linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            JLabel lbl = new JLabel(rotulo);
            lbl.setPreferredSize(new Dimension(28, 28));
            linha.add(lbl, BorderLayout.WEST);
            linha.add(campo, BorderLayout.CENTER);
            return linha;
        }

        private JComponent separador() {
            JPanel p = new JPanel();
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
            p.add(new JSeparator());
            return p;
        }

        void atualizar() {
            atualizandoCampos = true;
            try {
                if (selecionado instanceof NoItem) {
                    NoItem no = (NoItem)selecionado;
                    tituloNo.setText("Nó " + no.id);
                    setTexto(campoX, no.x);
                    setTexto(campoY, no.y);
                    setTexto(campoZ, no.z);
                    setTexto(campoFx, no.fx);
                    setTexto(campoFy, no.fy);
                    setTexto(campoFz, no.fz);
                    VinculoItem v = vinculos.get(no.id);
                    chkRx.setSelected(v != null && v.rx);
                    chkRy.setSelected(v != null && v.ry);
                    chkRz.setSelected(v != null && v.rz);
                    setTexto(campoReacaoX, v != null ? v.reacaoX : 0);
                    setTexto(campoReacaoY, v != null ? v.reacaoY : 0);
                    setTexto(campoReacaoZ, v != null ? v.reacaoZ : 0);
                    boolean temVinculo = v != null;
                    campoReacaoX.setEnabled(temVinculo);
                    campoReacaoY.setEnabled(temVinculo);
                    campoReacaoZ.setEnabled(temVinculo);
                    cards.show(conteudo, "no");
                } else if (selecionado instanceof BarraItem) {
                    BarraItem b = (BarraItem)selecionado;
                    String tipo = Math.abs(b.forcaInterna) < 1e-9 ? "sem resultado / nula" : b.forcaInterna > 0 ? "tração" : "compressão";
                    infoBarra.setText(String.format(
                        "<html><b>Barra %d</b><br><br>Nó inicial: %d<br>Nó final: %d<br><br>Força interna: %.3f N<br>Estado: %s</html>",
                        b.id, b.noA, b.noB, Math.abs(b.forcaInterna), tipo));
                    cards.show(conteudo, "barra");
                } else if (selecionado instanceof VinculoItem) {
                    VinculoItem v = (VinculoItem)selecionado;
                    tituloVinculo.setText("Vínculo no nó " + v.noId);
                    campoVinculoNo.setText(String.valueOf(v.noId));
                    chkVinculoRx.setSelected(v.rx);
                    chkVinculoRy.setSelected(v.ry);
                    chkVinculoRz.setSelected(v.rz);
                    setTexto(campoVinculoReacaoX, v.reacaoX);
                    setTexto(campoVinculoReacaoY, v.reacaoY);
                    setTexto(campoVinculoReacaoZ, v.reacaoZ);
                    chkVinculoRx.setEnabled(true);
                    chkVinculoRy.setEnabled(true);
                    chkVinculoRz.setEnabled(true);
                    campoVinculoReacaoX.setEnabled(true);
                    campoVinculoReacaoY.setEnabled(true);
                    campoVinculoReacaoZ.setEnabled(true);
                    cards.show(conteudo, "vinculo");
                } else {
                    cards.show(conteudo, "vazio");
                }
            } finally {
                atualizandoCampos = false;
            }
        }

        private void setTexto(JTextField campo, double valor) {
            String texto = String.format("%.4f", valor).replace(",", ".");
            if (!campo.getText().equals(texto)) campo.setText(texto);
        }

        private void aplicarNoSelecionado() {
            if (atualizandoCampos) return;
            if (!(selecionado instanceof NoItem)) return;
            try {
                NoItem no = (NoItem)selecionado;
                no.x = Double.parseDouble(campoX.getText().replace(",", "."));
                no.y = Double.parseDouble(campoY.getText().replace(",", "."));
                no.z = Double.parseDouble(campoZ.getText().replace(",", "."));
                no.fx = Double.parseDouble(campoFx.getText().replace(",", "."));
                no.fy = Double.parseDouble(campoFy.getText().replace(",", "."));
                no.fz = Double.parseDouble(campoFz.getText().replace(",", "."));

                if (chkRx.isSelected() || chkRy.isSelected() || chkRz.isSelected()) {
                    VinculoItem v = vinculos.get(no.id);
                    if (v == null) {
                        v = new VinculoItem(no.id);
                        vinculos.put(no.id, v);
                    }
                    v.rx = chkRx.isSelected();
                    v.ry = chkRy.isSelected();
                    v.rz = chkRz.isSelected();
                } else {
                    vinculos.remove(no.id);
                }
                atualizarAlgebra();
                espaco3D.repaint();
            } catch (NumberFormatException ignored) {
            }
        }

        private void aplicarVinculoSelecionado() {
            if (atualizandoCampos) return;
            if (!(selecionado instanceof VinculoItem)) return;
            try {
                VinculoItem vinculo = (VinculoItem)selecionado;
                int novoNoId = Integer.parseInt(campoVinculoNo.getText().trim());
                if (!nos.containsKey(novoNoId)) return;

                if (novoNoId != vinculo.noId) {
                    vinculos.remove(vinculo.noId);
                    vinculo.noId = novoNoId;
                    vinculos.put(vinculo.noId, vinculo);
                }

                vinculo.rx = chkVinculoRx.isSelected();
                vinculo.ry = chkVinculoRy.isSelected();
                vinculo.rz = chkVinculoRz.isSelected();

                atualizarAlgebra();
                espaco3D.repaint();
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private class PainelEspaco3D extends JPanel {
        private final double pitchMinimo = Math.toRadians(8);
        private final double pitchMaximo = Math.toRadians(78);
        private double yaw = Math.toRadians(-35);
        private double pitch = Math.toRadians(25);
        private double zoom = 70;
        private double panX = 0;
        private double panY = 0;
        private Point ultimoMouse;

        PainelEspaco3D() {
            setBackground(Color.WHITE);
            MouseAdapter mouse = new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    ultimoMouse = e.getPoint();
                }

                @Override public void mouseClicked(MouseEvent e) {
                    Object obj = capturar(e.getPoint());
                    if (obj != null) selecionarObjeto(obj);
                }

                @Override public void mouseDragged(MouseEvent e) {
                    if (ultimoMouse == null) return;
                    int dx = e.getX() - ultimoMouse.x;
                    int dy = e.getY() - ultimoMouse.y;
                    if (SwingUtilities.isRightMouseButton(e) || e.isShiftDown()) {
                        panX += dx;
                        panY += dy;
                    } else {
                        yaw += dx * 0.01;
                        pitch = limitarPitch(pitch - dy * 0.01);
                    }
                    ultimoMouse = e.getPoint();
                    repaint();
                }

                @Override public void mouseWheelMoved(MouseWheelEvent e) {
                    zoom = Math.max(18, Math.min(220, zoom * Math.pow(1.12, -e.getPreciseWheelRotation())));
                    repaint();
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
            addMouseWheelListener(mouse);
        }

        void resetarVista() {
            yaw = Math.toRadians(-35);
            pitch = Math.toRadians(25);
            zoom = 70;
            panX = 0;
            panY = 0;
            repaint();
        }

        private double limitarPitch(double valor) {
            return Math.max(pitchMinimo, Math.min(pitchMaximo, valor));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            desenharPlano(g2);
            desenharBarras(g2);
            desenharForcas(g2);
            desenharNos(g2);
            desenharAjuda(g2);
        }

        private void desenharPlano(Graphics2D g2) {
            int limite = 12;
            Polygon piso = new Polygon();
            adicionarPonto(piso, -limite, -limite, 0);
            adicionarPonto(piso, limite, -limite, 0);
            adicionarPonto(piso, limite, limite, 0);
            adicionarPonto(piso, -limite, limite, 0);
            g2.setColor(new Color(226, 226, 226, 150));
            g2.fillPolygon(piso);

            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(205, 205, 210));
            for (int i = -limite; i <= limite; i++) {
                linha3D(g2, -limite, i, 0, limite, i, 0);
                linha3D(g2, i, -limite, 0, i, limite, 0);
            }

            desenharEixo(g2, -limite, 0, 0, limite, 0, 0, new Color(220, 30, 30), "X");
            desenharEixo(g2, 0, -limite, 0, 0, limite, 0, new Color(20, 130, 35), "Y");
            desenharEixo(g2, 0, 0, -4, 0, 0, 8, new Color(40, 65, 220), "Z");
        }

        private void desenharEixo(Graphics2D g2, double x1, double y1, double z1, double x2, double y2, double z2, Color cor, String label) {
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(2.2f));
            linha3D(g2, x1, y1, z1, x2, y2, z2);
            Point p = projetar(x2, y2, z2);
            g2.drawString(label, p.x + 6, p.y - 6);
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            for (int i = (int)Math.ceil(Math.min(coordenadaEixo(x1, y1, z1, label), coordenadaEixo(x2, y2, z2, label)));
                 i <= (int)Math.floor(Math.max(coordenadaEixo(x1, y1, z1, label), coordenadaEixo(x2, y2, z2, label))); i++) {
                if (i == 0) continue;
                Point t = label.equals("X") ? projetar(i, 0, 0) : label.equals("Y") ? projetar(0, i, 0) : projetar(0, 0, i);
                g2.fillOval(t.x - 2, t.y - 2, 4, 4);
                g2.drawString(String.valueOf(i), t.x + 4, t.y + 12);
            }
        }

        private double coordenadaEixo(double x, double y, double z, String eixo) {
            if (eixo.equals("X")) return x;
            if (eixo.equals("Y")) return y;
            return z;
        }

        private void desenharBarras(Graphics2D g2) {
            for (BarraItem barra : barras.values()) {
                NoItem a = nos.get(barra.noA);
                NoItem b = nos.get(barra.noB);
                if (a == null || b == null) continue;
                boolean ativo = barra == selecionado;
                if (Math.abs(barra.forcaInterna) > 1e-9) {
                    g2.setColor(barra.forcaInterna > 0 ? new Color(0, 110, 200) : new Color(200, 30, 30));
                } else {
                    g2.setColor(ativo ? new Color(245, 150, 20) : new Color(42, 42, 48));
                }
                g2.setStroke(new BasicStroke(ativo ? 4f : 2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                linha3D(g2, a.x, a.y, a.z, b.x, b.y, b.z);
            }
        }

        private void desenharNos(Graphics2D g2) {
            for (NoItem no : nos.values()) {
                Point p = projetar(no.x, no.y, no.z);
                boolean ativo = no == selecionado;
                VinculoItem v = vinculos.get(no.id);
                int r = ativo ? 8 : 6;
                g2.setColor(new Color(0, 0, 0, 35));
                g2.fillOval(p.x - r + 2, p.y - r + 2, r * 2, r * 2);
                g2.setColor(v != null ? new Color(35, 130, 70) : new Color(35, 95, 210));
                g2.fillOval(p.x - r, p.y - r, r * 2, r * 2);
                g2.setColor(ativo ? new Color(245, 150, 20) : Color.WHITE);
                g2.setStroke(new BasicStroke(ativo ? 2.5f : 1.2f));
                g2.drawOval(p.x - r, p.y - r, r * 2, r * 2);
                g2.setColor(new Color(45, 45, 52));
                g2.drawString("N" + no.id, p.x + 8, p.y - 8);
            }
        }

        private void desenharForcas(Graphics2D g2) {
            g2.setStroke(new BasicStroke(2.2f));
            g2.setColor(new Color(170, 0, 170));
            for (NoItem no : nos.values()) {
                double mag = Math.sqrt(no.fx * no.fx + no.fy * no.fy + no.fz * no.fz);
                if (mag < 1e-9) continue;
                double escala = Math.min(1.2, 2.2 / mag);
                Point a = projetar(no.x, no.y, no.z);
                Point b = projetar(no.x + no.fx * escala, no.y + no.fy * escala, no.z + no.fz * escala);
                g2.drawLine(a.x, a.y, b.x, b.y);
                g2.fillOval(b.x - 4, b.y - 4, 8, 8);
            }
        }

        private void desenharAjuda(Graphics2D g2) {
            g2.setColor(new Color(110, 110, 118));
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            g2.drawString("Arraste para girar | Shift/btn direito para mover | roda para zoom | clique para selecionar", 14, getHeight() - 16);
        }

        private Object capturar(Point mouse) {
            Object melhor = null;
            double melhorDist = 14;
            for (NoItem no : nos.values()) {
                Point p = projetar(no.x, no.y, no.z);
                double d = p.distance(mouse);
                if (d < melhorDist) {
                    melhorDist = d;
                    melhor = no;
                }
            }
            for (BarraItem barra : barras.values()) {
                NoItem a = nos.get(barra.noA);
                NoItem b = nos.get(barra.noB);
                if (a == null || b == null) continue;
                Point pa = projetar(a.x, a.y, a.z);
                Point pb = projetar(b.x, b.y, b.z);
                double d = Line2D.ptSegDist(pa.x, pa.y, pb.x, pb.y, mouse.x, mouse.y);
                if (d < melhorDist) {
                    melhorDist = d;
                    melhor = barra;
                }
            }
            return melhor;
        }

        private void linha3D(Graphics2D g2, double x1, double y1, double z1, double x2, double y2, double z2) {
            Point a = projetar(x1, y1, z1);
            Point b = projetar(x2, y2, z2);
            g2.drawLine(a.x, a.y, b.x, b.y);
        }

        private void adicionarPonto(Polygon p, double x, double y, double z) {
            Point ponto = projetar(x, y, z);
            p.addPoint(ponto.x, ponto.y);
        }

        private Point projetar(double x, double y, double z) {
            // Projeção 3D simples em Java2D: rotaciona a cena e projeta no plano da tela.
            double cy = Math.cos(yaw);
            double sy = Math.sin(yaw);
            double cp = Math.cos(pitch);
            double sp = Math.sin(pitch);

            double xr = x * cy - y * sy;
            double yr = x * sy + y * cy;
            double zr = z;
            double yp = yr * cp + zr * sp;

            int sx = (int)Math.round(getWidth() / 2.0 + panX + xr * zoom);
            int sy2 = (int)Math.round(getHeight() / 2.0 + panY - yp * zoom);
            return new Point(sx, sy2);
        }
    }

    private static class NoItem {
        int id;
        double x;
        double y;
        double z;
        double fx;
        double fy;
        double fz;

        NoItem(int id, double x, double y, double z) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static class BarraItem {
        int id;
        int noA;
        int noB;
        double forcaInterna;

        BarraItem(int id, int noA, int noB) {
            this.id = id;
            this.noA = noA;
            this.noB = noB;
        }
    }

    private static class VinculoItem {
        int noId;
        boolean rx;
        boolean ry;
        boolean rz;
        double reacaoX;
        double reacaoY;
        double reacaoZ;

        VinculoItem(int noId) {
            this.noId = noId;
        }
    }
}
