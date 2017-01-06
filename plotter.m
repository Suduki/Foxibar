close all;
clear all;
load('filename.txt');
hold on

plot(median(filename(:,1)),median(filename(:,2)),'r*')
plot(mean(filename(:,1)),mean(filename(:,2)),'r.')
filename(:,1) = smooth(filename(:,1), 60);
filename(:,2) = smooth(filename(:,2), 60);
plot(filename(end*7/8:end,1),filename(end*7/8:end,2),'-')

