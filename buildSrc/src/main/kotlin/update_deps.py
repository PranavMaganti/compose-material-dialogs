import re
from typing import List
import bs4 as beautifulsoup
import requests
from tqdm import tqdm

from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as ec

main_search_url = 'https://search.maven.org/solrsearch/select?q=g:"{}"+AND+a:"{}"&core=gav&rows=1&wt=json'
google_base_url = "https://dl.google.com/android/maven2/"



# chrome_options = Options()
# # chrome_options.add_argument("--headless")
# chrome_options.add_argument("--window-size=1920x1080")
# driver = webdriver.Chrome("/opt/google/chrome-beta/google-chrome-beta", options=chrome_options)
#
dep_file = "Dependencies.kt"

with open(dep_file, "r") as readfile:
    current_deps = readfile.read().splitlines()

version_patten = re.compile(r"version = \"(.*)\"")
dep_pattern = re.compile(r"const val ([^=]*) = \"(.*)\"")
current_version = ("", None, "")

for index, item in enumerate(tqdm(current_deps)):



#
# updated_deps: List[str] = []
#
# for index, item in enumerate(tqdm(current_deps)):
#     version_match = version_patten.search(item)
#     dep_match = dep_pattern.search(item)
#
#     if version_match:
#         if current_version[1] != -1:
#             updated_deps[current_version[1]] = re.sub(version_patten, f'version = "{current_version[0]}"', current_version[2])
#         current_version = (version_match.group(1), index, item)
#         updated_deps.append(item)
#     elif dep_match:
#         (dep, version) = dep_match.group(2).rsplit(":", 1)
#         (group, artifact) = dep.split(":")
#         main_dep_url = main_search_url.format(group, artifact)
#         google_dep_url = f"{google_search_url}#{group}:{artifact}"
#
#         res = requests.get(main_dep_url).json()
#         latest_version = version
#
#         if res["response"]["docs"]:
#             latest_version = res["response"]["docs"][0]["v"]
#         else:
#             print(google_dep_url)
#             driver.get(google_dep_url)
#             elems = WebDriverWait(driver, 10).until(
#                 ec.visibility_of_element_located(
#                     (By.XPATH, "//div[@class='content-header ng-binding ng-scope']")
#                 )
#             )
#
#             soup = beautifulsoup.BeautifulSoup(driver.page_source, "html.parser")
#             latest_version = soup.find("span", {"class": "ng-binding"}).text
#
#         if version == "$version":
#             current_version = (latest_version, current_version[1], current_version[2])
#             updated_deps.append(item)
#         else:
#             updated_deps.append(
#                 f"const val {dep_match.group(1)} = \"{group}:{artifact}:{latest_version}\""
#             )
#     else:
#         updated_deps.append(item)
#
#
# if current_version[1] != -1:
#   updated_deps[current_version[1]] = re.sub(version_patten, f'version = "{current_version[0]}"', current_version[2])
#
# with open(dep_file, "w") as outfile:
#     outfile.write("\n".join(updated_deps))