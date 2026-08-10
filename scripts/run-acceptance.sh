#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

export DEPS_CLJ_TOOLS_DIR="${DEPS_CLJ_TOOLS_DIR:-$repo_root/.swarmforge/deps-clj}"
export CLJ_CONFIG="${CLJ_CONFIG:-$repo_root/.swarmforge/clojure}"
export GITLIBS="${GITLIBS:-$repo_root/.swarmforge/gitlibs}"
feature="acceptance/trace-atlas-source-contracts.feature"
ir="tmp/acceptance/ir/trace-atlas-source-contracts.json"
generated_dir="tmp/acceptance/generated"
generated="$generated_dir/trace-atlas-source-contracts.acceptance.clj"

mkdir -p "$(dirname "$ir")" "$generated_dir"

bb gherkin-parser "$feature" "$ir"
bb acceptance-entrypoint-generator "$ir" "$generated_dir"
bb "$generated"
