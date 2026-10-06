const fs = require('fs');
const path = require('path');

const emojiRegex = /[\u{1F300}-\u{1F64F}\u{1F680}-\u{1F6FF}\u{2600}-\u{26FF}\u{2700}-\u{27BF}\u{1F900}-\u{1F9FF}\u{1FA70}-\u{1FAFF}\u{1F000}-\u{1F02F}\u{1F0A0}-\u{1F0FF}\u{1F1E6}-\u{1F1FF}\u{1F200}-\u{1F251}\u{1F400}-\u{1F4FF}\u{1F500}-\u{1F5FF}\u{1F600}-\u{1F64F}\u{1F680}-\u{1F6FF}\u{1F700}-\u{1F77F}\u{1F780}-\u{1F7FF}\u{1F800}-\u{1F8FF}\u{1F900}-\u{1F9FF}\u{1FA00}-\u{1FA6F}\u{1FA70}-\u{1FAFF}\u{2702}-\u{27B0}\u{24C2}-\u{1F251}]/gu;

function removeEmojis(dir) {
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            removeEmojis(fullPath);
        } else if (/\.(html|js|css|java|sql|md)$/.test(file)) {
            let content = fs.readFileSync(fullPath, 'utf8');
            const newContent = content.replace(emojiRegex, '').replace(/👤|📱|🔒|👥|🌿|💰|🏘️|📅|📝|💬|📞|🔍|👨‍🌾|📦|⏳|➕|💾|📡|🌱|🛒|📄/g, '');
            if (newContent !== content) {
                fs.writeFileSync(fullPath, newContent, 'utf8');
                console.log('Removed emojis from:', fullPath);
            }
        }
    }
}

removeEmojis('c:/leonel rachid/Agretech/backend/src');
removeEmojis('c:/leonel rachid/Agretech/init_database.sql');
