# AvaliaApp Android - TODO

## Fase 1: Estrutura Base ✅
- [x] Setup Gradle e dependências
- [x] Configuração de tema Material 3
- [x] Estrutura de navegação
- [x] Models e DAOs
- [x] Room Database setup
- [x] API Service (Retrofit)
- [x] Repository pattern

## Fase 2: UI - Telas Principais
- [x] HomeScreen (landing page)
- [x] DashboardScreen (listagem)
- [ ] SurveyBuilderScreen (editor)
  - [ ] Form para criar/editar avaliação
  - [ ] Editor de perguntas com drag-and-drop
  - [ ] Tipos de campo (6 tipos)
  - [ ] Validação de campos obrigatórios
  - [ ] Botão publicar
- [ ] SurveyRespondScreen (responder)
  - [ ] Renderizar perguntas
  - [ ] Inputs para cada tipo
  - [ ] Validação de respostas
  - [ ] Envio de respostas
  - [ ] Tela de sucesso
- [ ] SurveyResultsScreen (resultados)
  - [ ] Gráficos de barras
  - [ ] Gráficos de pizza
  - [ ] Estatísticas
  - [ ] Botão exportar CSV

## Fase 3: Funcionalidades Offline
- [ ] Implementar sincronização offline-first
- [ ] Fila de sincronização
- [ ] Retry logic
- [ ] Indicador de status de sincronização
- [ ] Teste de offline mode

## Fase 4: Autenticação
- [ ] Fluxo OAuth Manus
- [ ] Token management
- [ ] Refresh token logic
- [ ] Logout
- [ ] Persistência de sessão

## Fase 5: Recursos Avançados
- [ ] Exportação CSV
- [ ] Compartilhamento via link
- [ ] Notificações locais
- [ ] Temas (claro/escuro)
- [ ] Múltiplos idiomas

## Fase 6: Testes e Otimização
- [ ] Unit tests
- [ ] Integration tests
- [ ] Performance testing
- [ ] Otimização de bateria
- [ ] Otimização de memória

## Fase 7: Release
- [ ] Build release
- [ ] ProGuard/R8 obfuscation
- [ ] Testes em dispositivos reais
- [ ] Preparar para Play Store
- [ ] Documentação de usuário

## Bugs Conhecidos
- [ ] Nenhum reportado ainda

## Melhorias Futuras
- [ ] Suporte a temas customizáveis
- [ ] Biometria para autenticação
- [ ] Backup automático
- [ ] Sincronização em background
- [ ] Widget para status de avaliações
