# -*- mode: ruby -*-
# vi: set ft=ruby :

###
### Vagrantfile to sets up a VM for gprdb build.
###

Vagrant.configure(2) do |config|
  # box
  config.vm.box = "bento/centos-7.1"

  # network
  config.vm.network "private_network", ip: "192.168.99.10"

  # VirtualBox specific settings
  config.vm.provider "virtualbox" do |vb|
    vb.memory = 2048
  end

  # provisioning
  config.vm.provision "shell", inline: <<-EOS
    sudo yum install -y java-1.8.0-openjdk-devel.x86_64
    sudo yum install -y epel-release
    sudo yum install -y docker
    sudo yum install -y python-pip

    sudo systemctl start docker
    sudo systemctl enable docker

    sudo pip install docker-compose

    repo=/etc/yum.repos.d/sonar.repo
    [ -f $repo ] || sudo wget -O $repo http://downloads.sourceforge.net/project/sonar-pkg/rpm/sonar.repo
    sudo yum install -y sonar
    sudo systemctl start sonar
    sudo chkconfig sonar on
  EOS
end
