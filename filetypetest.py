import filetype


def main():
    kind = filetype.guess('C:\\Users\\lihongyu\\Desktop\\新建文件夹\\0c9223f44fe08f988a349e764489579e74d3099793d643a6e71c9ffe889810e3.jpg')
    if kind is None:
        print('Cannot guess file type!')
        return
    print('File extension: %s' % kind.extension)
    print('File MIME type: %s' % kind.mime.split('/')[0])


if __name__ == '__main__':
    main()