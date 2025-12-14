
./.tree -I "build*|gradle*|tree*" \
  --dirsfirst | \
  sed "1s|^.|$(basename "$PWD")|"