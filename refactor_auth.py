import os

replacements = {
    "src/main/java/com/agritech/dondo/dto/LoginRequest.java": [
        ("private String telemovel;", "private String nome;"),
        ("public String getTelemovel()", "public String getNome()"),
        ("public void setTelemovel(String telemovel)", "public void setNome(String nome)"),
        ("this.telemovel = telemovel;", "this.nome = nome;")
    ],
    "src/main/java/com/agritech/dondo/model/Utilizador.java": [
        ('@Column(nullable = false, length = 100)', '@Column(nullable = false, unique = true, length = 100)')
    ],
    "src/main/java/com/agritech/dondo/repository/UtilizadorRepository.java": [
        ("Optional<Utilizador> findByTelemovel(String telemovel);", "Optional<Utilizador> findByNome(String nome);"),
        ("boolean existsByTelemovel(String telemovel);", "boolean existsByNome(String nome);")
    ],
    "src/main/java/com/agritech/dondo/service/CustomUserDetailsService.java": [
        ("public UserDetails loadUserByUsername(String telemovel)", "public UserDetails loadUserByUsername(String nome)"),
        ("findByTelemovel(telemovel)", "findByNome(nome)"),
        ("utilizador.getTelemovel()", "utilizador.getNome()"),
        ("telemvel: \" + telemovel", "nome: \" + nome")
    ],
    "src/main/java/com/agritech/dondo/config/JwtUtil.java": [
        ("public String generateToken(String telemovel)", "public String generateToken(String nome)"),
        (".subject(telemovel)", ".subject(nome)")
    ],
    "src/main/java/com/agritech/dondo/config/JwtAuthenticationFilter.java": [
        ("String telemovel = null;", "String nome = null;"),
        ("telemovel = jwtUtil.extractUsername(jwt);", "nome = jwtUtil.extractUsername(jwt);"),
        ("if (telemovel != null", "if (nome != null"),
        ("loadUserByUsername(telemovel);", "loadUserByUsername(nome);")
    ],
    "src/main/java/com/agritech/dondo/service/AuthService.java": [
        ("findByTelemovel(request.getTelemovel())", "findByNome(request.getNome())"),
        ("generateToken(utilizador.getTelemovel())", "generateToken(utilizador.getNome())"),
        ("existsByTelemovel(dto.getTelemovel())", "existsByNome(dto.getNome())"),
        ("generateToken(salvo.getTelemovel())", "generateToken(salvo.getNome())")
    ]
}

base_dir = "c:/leonel rachid/Agretech/backend/"

for file_path, changes in replacements.items():
    full_path = os.path.join(base_dir, file_path)
    if os.path.exists(full_path):
        with open(full_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        for old, new in changes:
            content = content.replace(old, new)
            
        with open(full_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {file_path}")
    else:
        print(f"Not found: {file_path}")

