import os
import json
from pathlib import Path

import requests


def get_newest_download_link():
    r = requests.get("https://api.github.com/repos/LittleMengBot/MengProject/releases/latest")
    return json.loads(r.text)['assets'][0]['browser_download_url']


if Path("release-amd64.jar").is_file():
    os.remove("release-amd64.jar")
os.system(f"wget {get_newest_download_link()}")
os.system("service meng restart")
