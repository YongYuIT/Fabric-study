cat log | while read line
do
  echo $line
  arr=($line)
  echo "${arr[0]}-->${arr[1]}-->${arr[2]}"
  docker tag ${arr[2]} ${arr[0]}:${arr[1]}
  echo "-----------------------------"
done
