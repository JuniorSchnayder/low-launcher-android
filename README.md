# Low-Launcher 📱🖤

Um inicializador Android minimalista focado em **Desintoxicação Digital**, **Redução de Tempo de Tela** e **Acesso Rápido Prioritário ao WhatsApp**.

---

## 🌟 Principais Recursos

1. **Grade Seletiva (Whitelist Estrita)**:
   * A tela inicial exibe apenas os aplicativos que você escolheu (recomendado: 4 a 6 apps).
   * Modo texto puro e limpo: remove ícones hipercoloridos que estimulam cliques impulsivos.

2. **Slot Exclusivo e Imediato para o WhatsApp**:
   * O WhatsApp possui um botão ergonômico no topo dos favoritos para verificação rápida.
   * **Fricção Zero**: Nunca passa por telas de pausa ou atrasos.
   * Sem notificações estressantes ou badges vermelhos de ansiedade.

3. **Pausa Reflexiva (*Mindful Friction*)**:
   * Para aplicativos potencialmente viciantes (ex: Instagram, TikTok, YouTube, jogos), o launcher introduz uma contagem regressiva suave (3 a 5 segundos) com a pergunta:
     > *"Respire fundo. Você realmente precisa abrir este aplicativo agora?"*
   * Interrompe o reflexo condicionado de abrir o aplicativo no piloto automático.

4. **Gaveta de Aplicativos com Busca Textual**:
   * Sem rolagem visual infinita de ícones.
   * Acesso aos demais apps por pesquisa instantânea via teclado (swipe para cima ou toque no rodapé).

5. **Métricas de Consciência**:
   * Exibição discreta do tempo de tela diário acumulado direto na Home.

6. **Design OLED Dark**:
   * Fundo preto puro (`#000000`) para máxima economia de bateria e conforto visual.

---

## 🏗 Arquitetura do Projeto

```text
Low-Launcher/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml          # Configuração de Launcher (CATEGORY_HOME)
│   │   ├── java/com/lowlauncher/app/
│   │   │   ├── MainActivity.kt          # Activity principal com navegação por gestos
│   │   │   ├── data/
│   │   │   │   ├── model/AppItem.kt     # Modelo de dados dos apps instalados
│   │   │   │   ├── preferences/         # Jetpack DataStore (whitelist e delays)
│   │   │   │   ├── repository/          # Leitura de apps via PackageManager
│   │   │   │   └── usage/               # Monitor de tempo de tela (UsageStatsManager)
│   │   │   └── ui/
│   │   │       ├── home/                # Tela Inicial (Clock, WhatsApp, Whitelist)
│   │   │       ├── drawer/              # Gaveta de busca textual instantânea
│   │   │       ├── friction/            # Diálogo de pausa respiratória
│   │   │       ├── settings/            # Tela de escolha de favoritos e tempos
│   │   │       └── theme/               # Tema minimalista monocromático
│   │   └── res/                         # Strings, cores e ícone adaptativo
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml               # Catálogo de dependências modernas
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 📦 Arquivo APK Gerado

O APK já foi compilado e está pronto para instalação:
* **[LowLauncher.apk](file:///c:/WS/Low-Launcher/LowLauncher.apk)** (copiado na raiz do projeto)
* Local de compilação original: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🚀 Como Instalar no Celular

### Opção 1: Direto pelo Celular (Mais fácil)
1. Envie o arquivo **`LowLauncher.apk`** para seu celular (por WhatsApp Web, Telegram, Google Drive, ou cabo USB).
2. No celular, toque no arquivo baixado e selecione **Instalar** (se solicitado, autorize "Instalar de fontes desconhecidas").
3. Pressione o botão **Home** do celular e selecione o **Low Launcher** como inicializador padrão.

### Opção 2: Via Cabo USB (ADB)
Com o celular conectado e *Depuração USB* ativada:
```bash
C:\WS\tools\android-sdk\platform-tools\adb.exe install -r LowLauncher.apk
```

---

## 🛠 Como Recompilar Futuramente

Basta dar dois cliques no script **`build-apk.bat`** na raiz da pasta do projeto. O script já está configurado com o ambiente Java 17 e Android SDK portátil.
