const fs = require('fs');
const path = require('path');

const replacements = {
    "src/main/java/com/agritech/dondo/dto/LoginRequest.java": [
        ["private String telemovel;", "private String nome;"],
        ["public String getTelemovel()", "public String getNome()"],
        ["public void setTelemovel(String telemovel)", "public void setNome(String nome)"],
        ["this.telemovel = telemovel;", "this.nome = nome;"]
    ],
    "src/main/java/com/agritech/dondo/model/Utilizador.java": [
        ['@Column(nullable = false, length = 100)', '@Column(nullable = false, unique = true, length = 100)']
    ],
    "src/main/java/com/agritech/dondo/repository/UtilizadorRepository.java": [
        ["Optional<Utilizador> findByTelemovel(String telemovel);", "Optional<Utilizador> findByNome(String nome);"],
        ["boolean existsByTelemovel(String telemovel);", "boolean existsByNome(String nome);"]
    ],
    "src/main/java/com/agritech/dondo/service/CustomUserDetailsService.java": [
        ["public UserDetails loadUserByUsername(String telemovel)", "public UserDetails loadUserByUsername(String nome)"],
        ["findByTelemovel(telemovel)", "findByNome(nome)"],
        ["utilizador.getTelemovel()", "utilizador.getNome()"],
        ['telem\u00f3vel: " + telemovel', 'nome: " + nome'],
        ['telem\u00e9vel: " + telemovel', 'nome: " + nome'],
        ['telemvel: " + telemovel', 'nome: " + nome']
    ],
    "src/main/java/com/agritech/dondo/config/JwtUtil.java": [
        ["public String generateToken(String telemovel)", "public String generateToken(String nome)"],
        [".subject(telemovel)", ".subject(nome)"]
    ],
    "src/main/java/com/agritech/dondo/config/JwtAuthenticationFilter.java": [
        ["String telemovel = null;", "String nome = null;"],
        ["telemovel = jwtUtil.extractUsername(jwt);", "nome = jwtUtil.extractUsername(jwt);"],
        ["if (telemovel != null", "if (nome != null"],
        ["loadUserByUsername(telemovel);", "loadUserByUsername(nome);"]
    ],
    "src/main/java/com/agritech/dondo/service/AuthService.java": [
        ["findByTelemovel(request.getTelemovel())", "findByNome(request.getNome())"],
        ["generateToken(utilizador.getTelemovel())", "generateToken(utilizador.getNome())"],
        ["existsByTelemovel(dto.getTelemovel())", "existsByNome(dto.getNome())"],
        ["generateToken(salvo.getTelemovel())", "generateToken(salvo.getNome())"]
    ]
};

const baseDir = "c:/leonel rachid/Agretech/backend/";

for (const [filePath, changes] of Object.entries(replacements)) {
    const fullPath = path.join(baseDir, filePath);
    if (fs.existsSync(fullPath)) {
        let content = fs.readFileSync(fullPath, 'utf8');
        for (const [oldStr, newStr] of changes) {
            content = content.split(oldStr).join(newStr);
        }
        fs.writeFileSync(fullPath, content, 'utf8');
        console.log("Updated", filePath);
    } else {
        console.log("Not found:", filePath);
    }
}
