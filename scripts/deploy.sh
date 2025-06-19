#!/bin/bash

echo "🚀 Déploiement Color Run en cours..."

echo "📦 Compilation du projet..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation"
    exit 1
fi

echo "⏹️  Arrêt de Tomcat..."
sudo systemctl stop tomcat10 2>/dev/null || echo "Tomcat n'était pas démarré"

echo "🗑️  Suppression de l'ancienne version..."
sudo rm -rf /var/lib/tomcat10/webapps/color_run-1.0-SNAPSHOT*

echo "📋 Déploiement de la nouvelle version..."
sudo cp target/color_run-1.0-SNAPSHOT.war /var/lib/tomcat10/webapps/

echo "🔄 Redémarrage de Tomcat..."
sudo systemctl start tomcat10

echo "⏳ Attente du déploiement..."
sleep 10

echo "🔍 Vérification du déploiement..."
if curl -s http://localhost:8080/color_run-1.0-SNAPSHOT/ > /dev/null; then
    echo "✅ Déploiement réussi !"
    echo "🌐 Application disponible à : http://localhost:8080/color_run-1.0-SNAPSHOT/"
else
    echo "⚠️  L'application ne semble pas répondre. Vérifiez les logs :"
    echo "   tail -f /var/lib/tomcat10/logs/catalina.out"
fi

echo "🏁 Fin du déploiement"