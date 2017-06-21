#!groovy
nodeWithProperWorkspace {

    stage('Checkout') {
        checkout scm
        if (isMaster()) {
            // git by default checks out detached, we need a local branch
            sh "git checkout $env.BRANCH_NAME" // workaround for https://issues.jenkins-ci.org/browse/JENKINS-31924
            sh 'git fetch --prune origin +refs/tags/*:refs/tags/*' // delete all local tags
            sh "git reset --hard origin/master"
            sh "git clean -ffdx"
        } else {
            sh "git clean -ffd"
        }
    }

    // { added some additional debug output to get hold of the failure to detect checkout only
    sh "git name-rev --name-only --tags HEAD" // this is used for detection of existing version tag
    sh "git name-rev --name-only --tags HEAD~1" // get some historical tags (if present) 
    sh "git name-rev --name-only --tags HEAD~2" 
    sh "git name-rev --name-only --tags HEAD~3" 
    sh "git name-rev --name-only --tags HEAD~4" 
    sh "git name-rev --all" // print all
    sh "git describe --tags" // describe the current tag
    sh "git status"
    def tag = bash('git name-rev --name-only --tags HEAD').trim()
    println tag
    // } end of added debug information
    
    if (isMaster() && localIsVersionTag()) {
        // Workaround: we don't want infinite releases.
        echo "Aborting build as the current commit on master is already tagged."
        currentBuild.displayName = "checkout-only"
        return
    }

    stage('Build') {
        withGradleEnv {
            gradle 'clean assemble'
        }
    }
    
    stage('Test') {
        withGradleEnv {
            withXvfb {
                gradle 'build'
            }
        }
    }

    if (isMaster()) {
        def preReleaseVersion = getVersion()
        stage('Release') {
            currentBuild.displayName = preReleaseVersion.replaceAll('-SNAPSHOT', '')
            withGradleEnv {
                sh 'git config user.email "jenkins@ci.testeditor.org"'
                sh 'git config user.name "jenkins"'
                // workaround: cannot push without credentials using HTTPS => push using SSH
                sh "git remote set-url origin ${getGithubUrlAsSsh()}"
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: '1e68e4c1-48a6-428c-8896-42511359493e', passwordVariable: 'BINTRAY_KEY', usernameVariable: 'BINTRAY_USER']]) {
                    gradle 'release'
                }
            }
        }
            
        stage('Increment develop version') {
            sh "git checkout develop"
            sh "git fetch origin"
            sh "git reset --hard origin/develop"
            def developVersion = getVersion()
            if (developVersion == preReleaseVersion) {
                sh "git merge origin/master"
                withGradleEnv {
                    gradle 'updateVersion -Prelease.useAutomaticVersion=true'
                }
                sh "git add ."
                sh "git commit -m '[release] set version ${getVersion()}'"
                sh "git push origin develop"
            } else {
                echo "Version on develop not incremented as it differs from the preReleaseVersion."
            }
        }
    }


String localIsVersionTag() {
    def versionPattern = /v\d+.\d+(.\d+)?(\^0)?/
    def tag = bash('git name-rev --name-only --tags HEAD~1').trim()
    return tag ==~ versionPattern
}

String getVersion() {
    def properties = readProperties file: 'gradle.properties'
    return properties.version
}
