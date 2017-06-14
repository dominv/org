#!/bin/sh

# Configure git

git config --global user.name "Travis CI"
git config --global user.email "domingo.v@47deg.com"

# Pull latest version of published subtree
git checkout master
git subtree pull --prefix=docs --message="[skip ci] Update subtree" https://dominv:$GITHUB_API_KEY@github.com/dominv/dominv.github.io.git master

# Build & Commit built site
lein run
git add docs
git commit -m "[skip ci] Generate site"
git push https://dominv:$GITHUB_API_KEY@github.com/dominv/org master

# Push built subtree to official website
git subtree push --prefix=docs https://dominv:$GITHUB_API_KEY@github.com/dominv/dominv.github.io.git master
