![Logo](https://cdn.discordapp.com/attachments/1184478625706278992/1186379127985541260/cuelingo_logo_green.png?ex=659308a9&is=658093a9&hm=9316550c61aabb63629d74dc1b5020aa4fa0c8762f221a0374a7782255e53539&)


# Cue Lingo : Our Sign Language Translator

This repository contains the model development code base that will be used to detect sign language in real time in the CueLingo Application.



### Method
Object Detection using Transfer Learning.

### Tools
Python
Mediapipeline
TensorFlow
NumPy
Pandas
Google Collab
Jupyter Notebook
Visual Studio Code

## DATASETS
We got the dataset by collecting data from various movements via meeting directly with various people who know SignLanguage and compete with BISINDO.

Link to Dataset : 
- [Dataset Cuelingo ](https://drive.google.com/drive/folders/1pMu7c-fGvr9S7Ce1iHjDGHScJgwRKE6R?usp=sharing)

## Classes Dataset
We Have 5 Main Classes with 34 Sub Class
1. 5w +1H
- Apa
- Bagaimana
- Dimana
- Kapan
- Siapa
- Mengapa

2. Aktivitas Harian
- Bangun Tidur
- Makan
- Minum 
- Pakai Baju
- Tidur

3. Anggota Keluarga
- Adik
- Bapak
- Bibi
- Ibu
- Kakak
- Kakek
- Nenek
- Paman

4. Hubungan Antar Manusia
- Aku
- Kami 
- Kamu
- Kita

5. Perlengkapan Sekolah
- Buku
- Penggaris
- Penghapus
- Pensil
- Pulpen
- Ransel


## DEPLOYMENT 
We use TensorFlow Lite (tflite) to deploy the trained model into android. .

## Our Reference

- Library Mediapipe, for training model Object Detection. [Mediapipe] (https://developers.google.com/mediapipe)
- BISINDO, Reference for Sign Languange. [pusbisindo.org] (https://pusbisindo.org/)
