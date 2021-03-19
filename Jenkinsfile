
node {
    stage('Initialize') {
		try {
			echo 'Initializing...'
			def maven=tool name: 'maven3.5.3', type: 'hudson.tasks.Maven$MavenInstallation'
			env.PATH = "${maven}/bin:${env.PATH}"
		}
		catch (exc) {
			slackSend color: '#ff0000', message: 'Backend: Stage <Initialize> failed'
			throw exc
		}
        
    }

    stage('Checkout') {
		try {
			echo 'Getting source code...'
			checkout scm
		}
		catch (exc) {
			slackSend color: '#ff0000', message: 'Backend: Stage <Checkout> failed'
			throw exc
		}
        
    }

    stage('Build') {
		try {
			sh 'mvn install' 
			sh 'mvn package'
		}
		catch (exc) {
			slackSend color: '#ff0000', message: 'Backend: Stage <Build> failed'
			throw exc
		}
        
    }
    
    stage('Build image') {
		try {
			app = docker.build("praha1889/g2gbackend")
		}
		catch (exc) {
			slackSend color: '#ff0000', message: 'Backend: Stage <Build image> failed'
			throw exc
		}
        
    }
    
    stage('Push Build') {
		try {
			docker.withRegistry('https://registry.hub.docker.com', 'docker-hub') {
				app.push("${env.BUILD_NUMBER}")
				app.push("latest")
			}
			sh 'docker image prune -af'
		}
		catch (exc) {
			slackSend color: '#ff0000', message: 'Backend: Stage <Push Build> failed'
			throw exc
		}
        
    }
    
    stage('Trigger Deployment') {
		try {
			echo 'Calling Kubernetes Master...'
			echo "New Build Number: ${env.BUILD_NUMBER}"
			sh "sshpass -p ${env.SSH_PASS_PW} ssh -o StrictHostKeyChecking=no ${env.SSH_PASS_USER}@${env.IP_MASTERNODE} 'kubectl set image deployment/g2g-backend g2g-backend=praha1889/g2gbackend:${env.BUILD_NUMBER}'"
			slackSend color: 'good', message: 'Backend: Build and uploading Image were successful. Deployment is triggered.'
		}
		catch (exc) {
			slackSend color: '#ff0000', message: 'Backend: Stage <Trigger Deployment> failed'
			throw exc
		}
        
    }

}

