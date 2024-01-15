import requests
import csv
import yaml
from pathlib import Path

# Define the paths
yaml_path = Path('src/main/resources/application.yaml')
csv_path = Path('src/main/resources/stickers.csv')

# Load the YAML configuration file
with open(yaml_path, 'r', encoding='utf-8') as yaml_file:
    config = yaml.safe_load(yaml_file)
    bot_token = config['bot']['token']
    sticker_set_names = config['bot']['sticker_sets']

# Clear the contents of the CSV file before writing new data
csv_path.unlink(missing_ok=True)

# Process each sticker set
for sticker_set_name in sticker_set_names:
    url = f"https://api.telegram.org/bot{bot_token}/getStickerSet?name={sticker_set_name}"
    response = requests.get(url)
    data = response.json()

    # Check if the request was successful
    if response.status_code == 200:
        # Check if 'result' and 'stickers' keys are in the response
        if 'result' in data and 'stickers' in data['result']:
            # Extract file IDs
            sticker_file_ids = [sticker['file_id'] for sticker in data['result']['stickers']]

            # Write to CSV
            with open(csv_path, mode='a', newline='', encoding='utf-8') as file:
                writer = csv.writer(file)
                # Write header if the file is being created
                if file.tell() == 0:
                    writer.writerow(['Sticker File ID'])
                for file_id in sticker_file_ids:
                    writer.writerow([file_id])

            print(f"Sticker IDs from set {sticker_set_name} appended to {csv_path}.")
        else:
            print(f"The 'result' or 'stickers' key is missing for set {sticker_set_name}.")
            print("Response data:", data)
    else:
        print(f"Failed to fetch data from the API for set {sticker_set_name}.")
        print("HTTP Status Code:", response.status_code)
