import re

with open("Dependencies.kt", "r") as readfile:
    current_deps = readfile.read().splitlines()

p = re.compile(r"version = \"(.*)\"")
current_version = ("", -1)

for index, item in enumerate(current_deps):
  version_match = p.search(item)
  if version_match:
      current_version = (version_match.group(1), index)
      continue

  
  
    
  
