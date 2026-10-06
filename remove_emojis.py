import os
import re
import emoji

def remove_emoji(text):
    return emoji.replace_emoji(text, replace='')

def process_dir(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(('.html', '.js', '.css', '.java', '.sql', '.md')):
                path = os.path.join(root, file)
                try:
                    with open(path, 'r', encoding='utf-8') as f:
                        content = f.read()
                    new_content = remove_emoji(content)
                    if new_content != content:
                        with open(path, 'w', encoding='utf-8') as f:
                            f.write(new_content)
                        print(f"Removed emojis from: {path}")
                except Exception as e:
                    pass

process_dir('c:/leonel rachid/Agretech/')
