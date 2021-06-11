# Simple Site

[![Build and Test](https://github.com/ManApart/simple-site/actions/workflows/runTests.yml/badge.svg)](https://github.com/ManApart/simple-site/actions/workflows/runTests.yml)

Slightly extend html in order to build sites that don't require javascript.


WebsiteBuilder builds your files.

To Autoreload, run SiteWatcher

```
npm i -g browser-sync
cd ./out
browser-sync start -s -f .
```
Navigate to localhost:3000


Manually copy your assets and css to the out folder for now

```
<for i="pet" src="pets">{{pet.name}} </for>
<include src="project.html" />
{{project.name}}
"<ifnotnull src=\"pets\">content</ifnotnull>
```

## TODO

Possible to do some sort of if logic?