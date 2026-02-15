# TXT Converter

A Spring Boot web application that converts plain text (.txt) files into CSV or Excel (.xlsx) format with custom separator support.

## Features

- 📄 **Drag & Drop Interface** - Easy file upload with drag-and-drop support
- 🔄 **Multiple Output Formats** - Convert to CSV or Excel (.xlsx)
- 🎯 **Custom Separators** - Choose from common separators (comma, pipe, semicolon, tab, space) or input custom ones
- ⚡ **Real-time Validation** - Form validation with instant feedback
- 🛡️ **Rate Limiting** - 5 uploads per minute per IP address to prevent abuse
- 💫 **Modern UI** - Glass morphism design with smooth animations
- 📥 **Auto Download** - Converted files automatically download to your device

## Tech Stack

**Backend:**
- Java 21
- Spring Boot 4.0.2
- Apache POI 5.5.1 (Excel generation)
- Bucket4j 8.16.1 (Rate limiting)

**Frontend:**
- HTML5
- CSS3 (Glass morphism effects)
- Vanilla JavaScript (AJAX, Drag & Drop API)

## Project Structure

```
src/main/
├── java/com/txt/convertor/
│   ├── ConvertorApplication.java       # Spring Boot entry point
│   ├── controller/
│   │   └── FileController.java         # REST endpoints
│   ├── service/
│   │   ├── ConversionService.java      # Service interface
│   │   └── impl/
│   │       └── ConversionServiceImpl.java # CSV & Excel conversion logic
│   └── config/
│       └── RateLimitFilter.java        # Rate limiting configuration
└── resources/
    ├── templates/
    │   └── index.html                  # Main UI
    └── static/
        ├── css/styles.css              # Styling
        └── js/script.js                # Client-side logic
```

## Installation & Setup

### Prerequisites
- Java 21 or higher
- Maven 3.9.12+ (wrapper included)

### Build & Run

```bash
# Clone the repository
git clone https://github.com/aditraj01/txt-to-csv-excel-convertor.git
cd txt-to-csv-excel-convertor

# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## Usage

1. **Open the Application** - Navigate to `http://localhost:8080`
2. **Upload a File** - Drag & drop a `.txt` file or click to browse
3. **Choose Separator** - Select from preset options or type a custom separator
4. **Select Output Format** - Choose between CSV or Excel
5. **Convert** - Click "Convert File" button
6. **Download** - The converted file automatically downloads

## API Endpoints

### GET `/`
Returns the main HTML page with the converter interface.

### POST `/convert`
Converts the uploaded TXT file to CSV or Excel format.

**Parameters:**
- `file` (MultipartFile) - The .txt file to convert
- `separator` (String) - The separator character(s)
- `type` (String) - Output format: `csv` or `excel`

**Response:**
- Status 200: File downloaded with appropriate `Content-Disposition` header
- Status 429: Rate limit exceeded (5 uploads per minute per IP)
- Status 400: Validation error

**Example:**
```bash
curl -X POST http://localhost:8080/convert \
  -F "file=@data.txt" \
  -F "separator=," \
  -F "type=csv"
```

## Rate Limiting

The application enforces a rate limit of **5 uploads per minute per IP address** using Bucket4j.

- Rate limit info is sent via `X-Rate-Limit-Remaining` header
- Exceeding the limit returns a 429 status with error message
- Limit resets automatically after 1 minute

## File Format Requirements

### Input
- File extension: `.txt`
- Expected format: Structured data with consistent separators

### Output
- **CSV**: Plain text with comma-separated values
- **Excel**: `.xlsx` format with proper cell formatting

## Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
spring.application.name=convertor
# Add additional properties as needed
```

## Features in Detail

### Frontend Validation
- File type checking (`.txt` only)
- Separator requirement validation
- Output format requirement validation
- Real-time disable/enable of convert button

### Separator Hints
Quick-select chips for common separators:
- Comma (`,`)
- Pipe (`|`)
- Semicolon (`;`)
- Tab (`\t`)
- Space (` `)

### Error Handling
- File validation (extension, size, content)
- Separator validation (not empty)
- Type validation (csv or excel)
- User-friendly error messages with status indicators

## Browser Support

- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Requires JavaScript enabled

## Future Enhancements

- [ ] Support for more file formats (JSON, XML)
- [ ] Batch file conversion
- [ ] Custom column mapping
- [ ] File preview before conversion
- [ ] Integration with cloud storage (Google Drive, OneDrive)
- [ ] User authentication & conversion history
- [ ] Advanced rate limiting configurations

## Troubleshooting

**Issue: "Only .txt files are allowed"**
- Ensure your file has a `.txt` extension
- Check file MIME type settings

**Issue: "Rate limit exceeded"**
- Wait 1 minute for the limit to reset
- The frontend shows remaining uploads in console logs

**Issue: "Conversion failed"**
- Verify the file format and separator choice
- Check server logs for detailed errors
- Ensure sufficient disk space

## License

This project is open source and available under the MIT License.

## Author

**Aditraj** - [GitHub Profile](https://github.com/aditraj01)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

**Repository:** [txt-to-csv-excel-convertor](https://github.com/aditraj01/txt-to-csv-excel-convertor)