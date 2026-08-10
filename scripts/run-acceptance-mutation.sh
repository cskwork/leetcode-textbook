#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

export DEPS_CLJ_TOOLS_DIR="${DEPS_CLJ_TOOLS_DIR:-$repo_root/.swarmforge/deps-clj}"
export CLJ_CONFIG="${CLJ_CONFIG:-$repo_root/.swarmforge/clojure}"
export GITLIBS="${GITLIBS:-$repo_root/.swarmforge/gitlibs}"

bash scripts/run-acceptance.sh
bash scripts/run-aps-tool.sh gherkin-mutator \
  --feature acceptance/trace-atlas-source-contracts.feature \
  --work-dir tmp/acceptance-mutation \
  --generated-dir tmp/acceptance/generated \
  --runner-worker scripts/run-acceptance-worker.sh \
  --workers 8 \
  --status-interval 5s \
  --level soft
