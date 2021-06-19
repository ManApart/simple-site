# Simple Site

[![Build and Test](https://github.com/ManApart/simple-site/actions/workflows/runTests.yml/badge.svg)](https://github.com/ManApart/simple-site/actions/workflows/runTests.yml)

Slightly extend html in order to build sites that don't require javascript.

## Website BUilder

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

## Blog Builder

Build out index files from markdown source.
Create a config file in your blog directory, then in this app's config, add the path to the folder that holds that config.
In this app's config create something like: `"blogPath": "workspace\\website"`


In the site's config, create something like:
```
{
  "blogs": "blog",
  "tabTitle": "Home",
  "toc": false,
  "tocTitle": "Table of Contents"
}
```

Property | Value
--- | ---
blogs | sub path where your blog markdown sources are
tabTitle | Title of tab for full index
toc | generate a table of contents

## TODO

Possible to do some sort of if logic?

Blog Builder
- TOC?
  - No Js scroll to / jump to option?