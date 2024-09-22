# Projeto Robo Brenfeer

## Descrição
O Projeto Robo Brenfeer é uma aplicação de web scraping desenvolvida para automatizar a extração de dados de um site específico. Este repositório contém todos os arquivos necessários para executar o robô, incluindo o executável .jar, arquivos de configuração e o driver do Chrome.

## Estrutura do Diretório
O conteúdo deste repositório deve ser extraído na seguinte localização:


Dentro deste diretório principal, você encontrará as seguintes subpastas:

- **bin:** Contém o arquivo `atualizar.xml`, que é utilizado para acessar mo servidor de download e procurar o arquivo para baixar no computador, e o executável `robo-scraping.jar`, que é o robô em si.

- **instalador:** Contém o arquivo `instalacao do java`, que é um script para extrair o java e configurar as variaveis de ambiente. Temos tambem o arquivo .zip da ersao do java.

- **planilhas:** Contém as planilhas responsaveis por ler os produtos para fazer o scraping `produtos.xlsx`, e também o arquivo `ovdresultado.xlsx` que é o arquivo que terá os dados capturados do site.

- **config:** Inclui o arquivo `arquivo-acesso.xml`, que contém os dados necessários para o scraping, como a URL do site, nome de usuário e senha.

- **webdriver:** Contém o ChromeDriver, necessário para a execução do scraping no navegador Chrome.

## Instruções de Uso

1. Extrair a pasta para `C:\Projeto Robo Brenfeer`.
2. Exzecutar o arquivo `instalacao do java.bat` que está na pasta `C:\Projeto Robo Brenfeer\instalador`.
3. Execute o arquivo `robo-scraping.jar` na pasta `bin` para iniciar o robô.

## Notas Adicionais
- Este projeto utiliza web scraping, certifique-se de cumprir as políticas de uso do site alvo.
- Recomenda-se manter os arquivos de configuração seguros e não compartilhá-los indiscriminadamente.

Esperamos que o Projeto Robo Brenfeer seja útil para suas necessidades de web scraping!
