#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

export DEPS_CLJ_TOOLS_DIR="${DEPS_CLJ_TOOLS_DIR:-$repo_root/.swarmforge/deps-clj}"
export CLJ_CONFIG="${CLJ_CONFIG:-$repo_root/.swarmforge/clojure}"
export GITLIBS="${GITLIBS:-$repo_root/.swarmforge/gitlibs}"

exec bb -m trace-atlas.acceptance-worker
