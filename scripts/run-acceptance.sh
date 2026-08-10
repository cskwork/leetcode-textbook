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

mkdir -p "$(dirname "$ir")" "$generated_dir"

bb gherkin-parser "$feature" "$ir"
generated="$(bb acceptance-entrypoint-generator "$ir" "$generated_dir")"
if [[ ! -f "$generated" ]]; then
  echo "Acceptance generator did not return a runnable entrypoint: $generated" >&2
  exit 2
fi
bb "$generated"
