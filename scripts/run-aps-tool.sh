#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
aps_dir="$repo_root/.swarmforge/tools/Acceptance-Pipeline-Specification"
aps_sha="accaa33d503340c56513ef387258f8da929ba902"
aps_url="https://github.com/unclebob/Acceptance-Pipeline-Specification.git"

if [[ ! -e "$aps_dir/.git" ]]; then
  if [[ -e "$aps_dir" ]]; then
    echo "APS tool path exists but is not a Git checkout: $aps_dir" >&2
    exit 2
  fi
  mkdir -p "$(dirname "$aps_dir")"
  git clone --filter=blob:none "$aps_url" "$aps_dir"
fi

if ! git -C "$aps_dir" cat-file -e "$aps_sha^{commit}" 2>/dev/null; then
  git -C "$aps_dir" fetch --depth 1 origin "$aps_sha"
fi

if [[ "$(git -C "$aps_dir" rev-parse HEAD)" != "$aps_sha" ]]; then
  git -C "$aps_dir" checkout --detach "$aps_sha"
fi

exec bb --config "$aps_dir/bb.edn" "$@"
