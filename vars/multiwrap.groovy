def multiwrap(wrappers, body) {
    _multiwrap(wrappers, 0, body)
}
def _multiwrap(wrappers, idx, body) {
    if (idx == wrappers.size()) {
        body()
    } else {
        wrap(wrappers.get(idx)) {
            _multiwrap(wrappers, idx + 1, body)
        }
    }
}
node {
    multiwrap([[$class: 'TimestamperBuildWrapper'],
               [$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'myfile', variable: 'FILE']]]]) {
        sh 'cat $FILE'
    }
}
